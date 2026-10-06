package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ZentrixDatabase
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity
import com.example.data.repository.KeyType
import com.example.data.repository.ZentrixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ConsoleTab(val title: String, val bnTitle: String) {
    PROJECTS("Projects", "প্রজেক্টসমূহ"),
    SDK_CONNECT("SDK & Keys", "কানেকশন ও কি"),
    VAULT("Secret Vault", "সিক্রেট ভল্ট"),
    AUTH("Auth & Users", "ব্যবহারকারী"),
    DATABASE("Database", "ডাটাবেস"),
    SECURITY("Security Rules", "সিকিউরিটি রুলস"),
    ANALYTICS("Logs & Metrics", "অ্যানালিটিক্স ও লগ")
}

data class RuleSimulationResult(
    val allowed: Boolean,
    val matchedRule: String,
    val details: String
)

class ZentrixViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ZentrixRepository

    init {
        val db = ZentrixDatabase.getDatabase(application)
        repository = ZentrixRepository(db.zentrixDao())
        viewModelScope.launch {
            repository.ensureDataPopulated()
        }
    }

    // Navigation and Active Selection
    private val _currentTab = MutableStateFlow(ConsoleTab.PROJECTS)
    val currentTab: StateFlow<ConsoleTab> = _currentTab.asStateFlow()

    private val _selectedProjectId = MutableStateFlow("zentrix-esport")
    val selectedProjectId: StateFlow<String> = _selectedProjectId.asStateFlow()

    // Revealed Secrets Visibility map (secretId -> isVisible)
    private val _revealedSecrets = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val revealedSecrets: StateFlow<Map<Long, Boolean>> = _revealedSecrets.asStateFlow()

    // Selected Collection for Database Tab
    private val _selectedCollection = MutableStateFlow<String?>(null)
    val selectedCollection: StateFlow<String?> = _selectedCollection.asStateFlow()

    // Search queries
    val searchQuery = MutableStateFlow("")

    // Notification toast / message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Dialog flags
    val showCreateProjectDialog = MutableStateFlow(false)
    val showAddSecretDialog = MutableStateFlow(false)
    val showAddUserDialog = MutableStateFlow(false)
    val showAddDocDialog = MutableStateFlow(false)
    val showSimulateRuleDialog = MutableStateFlow(false)

    // Data streams from repository
    val allProjects: StateFlow<List<ProjectEntity>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProject: StateFlow<ProjectEntity?> = combine(allProjects, selectedProjectId) { projects, id ->
        projects.find { it.id == id } ?: projects.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val vaultSecrets: StateFlow<List<VaultSecretEntity>> = combine(
        repository.getAllSecrets(),
        selectedProjectId
    ) { secrets, projId ->
        secrets.filter { it.projectId == "global" || it.projectId == projId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authUsers: StateFlow<List<AuthUserEntity>> = combine(
        repository.getAllUsers(),
        selectedProjectId
    ) { users, projId ->
        users.filter { it.projectId == projId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collections: StateFlow<List<String>> = combine(
        repository.getAllDocumentsForProject("zentrix-esport"),
        selectedProjectId
    ) { _, projId ->
        // will be dynamically updated by combining all docs
        emptyList<String>()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectDocuments: StateFlow<List<DataDocumentEntity>> = combine(
        allProjects,
        selectedProjectId,
        selectedCollection
    ) { _, projId, collectionName ->
        projId
    }.let {
        repository.getAllDocumentsForProject("zentrix-esport")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Dynamic Documents flow based on selected project
    val documentsForSelectedProject: StateFlow<List<DataDocumentEntity>> = combine(
        repository.getAllProjects(),
        selectedProjectId
    ) { _, projId ->
        projId
    }.combine(repository.getAllAuditLogs()) { projId, _ ->
        projId
    }.let {
        // We'll read documents with a combined state flow
        MutableStateFlow<List<DataDocumentEntity>>(emptyList())
    }

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe documents for current project
        viewModelScope.launch {
            combine(selectedProjectId, repository.getAllProjects()) { id, _ -> id }.collect { id ->
                repository.getAllDocumentsForProject(id).collect { docs ->
                    (documentsForSelectedProject as MutableStateFlow).value = docs
                    if (_selectedCollection.value == null && docs.isNotEmpty()) {
                        _selectedCollection.value = docs.first().collectionName
                    }
                }
            }
        }
    }

    // Tab Navigation
    fun setTab(tab: ConsoleTab) {
        _currentTab.value = tab
    }

    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
        _selectedCollection.value = null
        showToast("সক্রিয় প্রজেক্ট পরিবর্তন হয়েছে: $projectId")
    }

    fun selectCollection(name: String) {
        _selectedCollection.value = name
    }

    fun toggleSecretVisibility(id: Long) {
        val current = _revealedSecrets.value
        val isVisible = current[id] ?: false
        _revealedSecrets.value = current + (id to !isVisible)
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Project Actions
    fun createProject(
        name: String,
        bnName: String,
        description: String,
        category: String,
        publishStatus: String,
        environment: String
    ) {
        viewModelScope.launch {
            val created = repository.createProject(
                name = name,
                bnName = bnName,
                description = description,
                category = category,
                publishStatus = publishStatus,
                environment = environment
            )
            _selectedProjectId.value = created.id
            showToast("নতুন প্রজেক্ট তৈরি সফল: ${created.name}")
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            val remaining = allProjects.value.filter { it.id != projectId }
            if (remaining.isNotEmpty()) {
                _selectedProjectId.value = remaining.first().id
            }
            showToast("প্রজেক্ট মুছে ফেলা হয়েছে")
        }
    }

    fun rotateKey(projectId: String, keyType: KeyType) {
        viewModelScope.launch {
            val project = activeProject.value ?: return@launch
            val newKey = repository.regenerateProjectKey(projectId, keyType)
            val updated = when (keyType) {
                KeyType.ADMIN -> project.copy(adminKey = newKey)
                KeyType.STAFF -> project.copy(staffKey = newKey)
                KeyType.CLIENT -> project.copy(clientPublicKey = newKey)
            }
            repository.updateProject(updated)
            showToast("${keyType.name} Key সফলভাবে রিনিউ হয়েছে!")
        }
    }

    // Secret Vault Actions
    fun addSecret(
        keyName: String,
        secretValue: String,
        category: String,
        description: String,
        isGlobal: Boolean
    ) {
        viewModelScope.launch {
            val targetProj = if (isGlobal) "global" else _selectedProjectId.value
            repository.addSecret(
                projectId = targetProj,
                keyName = keyName,
                secretValue = secretValue,
                category = category,
                description = description
            )
            showToast("সিক্রেট ভল্টে সেভ হয়েছে: $keyName")
        }
    }

    fun deleteSecret(secret: VaultSecretEntity) {
        viewModelScope.launch {
            repository.deleteSecret(secret.id, secret.keyName, secret.projectId)
            showToast("সিক্রেট মুছে ফেলা হয়েছে: ${secret.keyName}")
        }
    }

    // Auth User Actions
    fun addUser(email: String, displayName: String, role: String, provider: String) {
        viewModelScope.launch {
            repository.createUser(
                projectId = _selectedProjectId.value,
                email = email,
                displayName = displayName,
                role = role,
                provider = provider
            )
            showToast("ব্যবহারকারী যুক্ত হয়েছে: $displayName ($role)")
        }
    }

    fun updateUserRole(uid: String, role: String) {
        viewModelScope.launch {
            repository.updateUserRole(uid, _selectedProjectId.value, role)
            showToast("ইউজার রোল আপডেট হয়েছে: $role")
        }
    }

    fun toggleUserBan(user: AuthUserEntity) {
        viewModelScope.launch {
            repository.toggleUserBan(user)
            val status = if (user.status == "BANNED") "আনব্যান" else "ব্যান"
            showToast("ইউজারকে $status করা হয়েছে")
        }
    }

    fun deleteUser(user: AuthUserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user.uid, user.projectId)
            showToast("ইউজার অ্যাকাউন্ট মুছে ফেলা হয়েছে")
        }
    }

    // Database Actions
    fun addDocument(collectionName: String, title: String, dataJson: String) {
        viewModelScope.launch {
            repository.addDocument(
                projectId = _selectedProjectId.value,
                collectionName = collectionName,
                title = title,
                dataJson = dataJson
            )
            _selectedCollection.value = collectionName
            showToast("ডকুমেন্ট সফলভাবে সংরক্ষণ হয়েছে")
        }
    }

    fun deleteDocument(doc: DataDocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(doc.id, doc.projectId, doc.collectionName)
            showToast("ডকুমেন্ট মুছে ফেলা হয়েছে")
        }
    }

    // Security Rules Action
    fun updateSecurityRules(rules: String) {
        viewModelScope.launch {
            val project = activeProject.value ?: return@launch
            repository.updateProject(project.copy(securityRules = rules))
            repository.recordLog(
                projectId = project.id,
                action = "SECURITY_RULES_DEPLOYED",
                actor = "Master Admin",
                details = "Deployed updated cloud security rules v2."
            )
            showToast("সিকিউরিটি রুলস লাইভ সার্ভারে ডিপ্লয় সম্পন্ন!")
        }
    }

    fun simulateRule(role: String, operation: String, path: String): RuleSimulationResult {
        // Realistic evaluator simulation for rule tester
        return when {
            role == "ADMIN" -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow read, write: if request.auth.role == 'ADMIN'",
                details = "Access Granted: Master Admin has superuser bypass on cluster."
            )
            role == "STAFF" && (path.contains("tournament") || path.contains("match") || path.contains("catalog")) -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow read, write: if request.auth.role in ['ADMIN', 'STAFF']",
                details = "Access Granted: Staff permissions verified for gaming & tournament operations."
            )
            role == "STAFF" && (path.contains("vault") || path.contains("secret") || path.contains("payment_key")) -> RuleSimulationResult(
                allowed = false,
                matchedRule = "allow read, write: if request.auth.role == 'ADMIN'",
                details = "Access Denied: Staff token is forbidden from accessing Admin Master Vault."
            )
            role == "USER" && operation == "READ" && (path.contains("tournament") || path.contains("catalog") || path.contains("leaderboard")) -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow read: if true;",
                details = "Access Granted: Public read policy active on live game data."
            )
            role == "USER" && operation == "WRITE" && path.contains("payment") -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow create: if request.auth != null;",
                details = "Access Granted: Authenticated client allowed to submit payment receipts."
            )
            else -> RuleSimulationResult(
                allowed = false,
                matchedRule = "default: deny all;",
                details = "Access Denied: No permissive policy matches for $role doing $operation on $path"
            )
        }
    }
}
