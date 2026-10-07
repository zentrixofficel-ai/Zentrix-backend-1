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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ConsoleTab(val title: String, val subtitle: String) {
    PROJECTS("Projects", "Manage Apps"),
    SDK_CONNECT("SDK & Keys", "Code Snippets"),
    VAULT("Secret Vault", "API Keys & Gateways"),
    AUTH("Auth & Users", "Accounts & Access"),
    DATABASE("Database", "Collections & Tables"),
    SECURITY("Security Rules", "Zero-Trust Policies"),
    DATA_EXPLORER("Data Explorer", "All Saved Records"),
    ANALYTICS("Logs & Metrics", "Audit Trail"),
    SETTINGS("Console Settings", "Database & Reset")
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
    }

    // Navigation Tab
    private val _currentTab = MutableStateFlow(ConsoleTab.PROJECTS)
    val currentTab: StateFlow<ConsoleTab> = _currentTab.asStateFlow()

    // Selected Project ID
    private val _selectedProjectId = MutableStateFlow<String>("")
    val selectedProjectId: StateFlow<String> = _selectedProjectId.asStateFlow()

    // Secrets Visibility Map (secretId -> isVisible)
    private val _revealedSecrets = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val revealedSecrets: StateFlow<Map<Long, Boolean>> = _revealedSecrets.asStateFlow()

    // Selected Collection for Database Tab
    private val _selectedCollection = MutableStateFlow<String?>(null)
    val selectedCollection: StateFlow<String?> = _selectedCollection.asStateFlow()

    // Notification toast / snackbar message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Dialog state flags
    val showCreateProjectDialog = MutableStateFlow(false)
    val showAddSecretDialog = MutableStateFlow(false)
    val showAddUserDialog = MutableStateFlow(false)
    val showAddDocDialog = MutableStateFlow(false)

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

    val allGlobalSecrets: StateFlow<List<VaultSecretEntity>> = repository.getAllSecrets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authUsers: StateFlow<List<AuthUserEntity>> = combine(
        repository.getAllUsers(),
        selectedProjectId
    ) { users, projId ->
        users.filter { it.projectId == projId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGlobalUsers: StateFlow<List<AuthUserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All documents across the entire backend (for Data Explorer)
    val allDocumentsAcrossProjects: StateFlow<List<DataDocumentEntity>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Documents for currently active project
    val documentsForSelectedProject = MutableStateFlow<List<DataDocumentEntity>>(emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically sync active project selection and documents
        viewModelScope.launch {
            allProjects.collect { projects ->
                if (_selectedProjectId.value.isBlank() && projects.isNotEmpty()) {
                    _selectedProjectId.value = projects.first().id
                }
            }
        }

        viewModelScope.launch {
            selectedProjectId.collect { id ->
                if (id.isNotBlank()) {
                    repository.getAllDocumentsForProject(id).collect { docs ->
                        documentsForSelectedProject.value = docs
                        if (_selectedCollection.value == null && docs.isNotEmpty()) {
                            _selectedCollection.value = docs.first().collectionName
                        }
                    }
                } else {
                    documentsForSelectedProject.value = emptyList()
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
        showToast("Switched active project: $projectId")
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
        appName: String,
        packageName: String,
        versionName: String,
        description: String,
        category: String,
        publishStatus: String,
        environment: String
    ) {
        viewModelScope.launch {
            val created = repository.createProject(
                name = name,
                appName = appName,
                packageName = packageName,
                versionName = versionName,
                description = description,
                category = category,
                publishStatus = publishStatus,
                environment = environment
            )
            _selectedProjectId.value = created.id
            showToast("Project '${created.appName}' created successfully!")
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            val remaining = allProjects.value.filter { it.id != projectId }
            _selectedProjectId.value = remaining.firstOrNull()?.id ?: ""
            showToast("Project deleted from cluster.")
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
            showToast("${keyType.name} Key rotated and updated!")
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
            val targetProj = if (isGlobal || _selectedProjectId.value.isBlank()) "global" else _selectedProjectId.value
            repository.addSecret(
                projectId = targetProj,
                keyName = keyName,
                secretValue = secretValue,
                category = category,
                description = description
            )
            showToast("Vault secret '$keyName' saved successfully.")
        }
    }

    fun deleteSecret(secret: VaultSecretEntity) {
        viewModelScope.launch {
            repository.deleteSecret(secret.id, secret.keyName, secret.projectId)
            showToast("Secret '${secret.keyName}' removed.")
        }
    }

    // Auth User Actions
    fun addUser(email: String, phoneNumber: String, displayName: String, role: String, provider: String) {
        viewModelScope.launch {
            val projId = _selectedProjectId.value.ifBlank { "global" }
            repository.createUser(
                projectId = projId,
                email = email,
                phoneNumber = phoneNumber,
                displayName = displayName,
                role = role,
                provider = provider
            )
            showToast("User '$displayName' registered ($role).")
        }
    }

    fun updateUserRole(uid: String, role: String) {
        viewModelScope.launch {
            repository.updateUserRole(uid, _selectedProjectId.value, role)
            showToast("User role updated to $role.")
        }
    }

    fun toggleUserBan(user: AuthUserEntity) {
        viewModelScope.launch {
            repository.toggleUserBan(user)
            val status = if (user.status == "BLOCKED" || user.status == "BANNED") "Unblocked" else "Blocked"
            showToast("User '${user.displayName}' is now $status.")
        }
    }

    fun bulkBlockUsers(uids: List<String>) {
        viewModelScope.launch {
            val projId = _selectedProjectId.value.ifBlank { "global" }
            repository.bulkBlockUsers(uids, projId)
            showToast("Bulk blocked ${uids.size} users.")
        }
    }

    fun bulkUnblockUsers(uids: List<String>) {
        viewModelScope.launch {
            val projId = _selectedProjectId.value.ifBlank { "global" }
            repository.bulkUnblockUsers(uids, projId)
            showToast("Bulk unblocked ${uids.size} users.")
        }
    }

    fun bulkDeleteUsers(uids: List<String>) {
        viewModelScope.launch {
            val projId = _selectedProjectId.value.ifBlank { "global" }
            repository.bulkDeleteUsers(uids, projId)
            showToast("Bulk deleted ${uids.size} users.")
        }
    }

    fun deleteUser(user: AuthUserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user.uid, user.projectId)
            showToast("User account deleted.")
        }
    }

    // Database Actions
    fun addDocument(collectionName: String, title: String, dataJson: String) {
        viewModelScope.launch {
            val projId = _selectedProjectId.value.ifBlank { "default-project" }
            repository.addDocument(
                projectId = projId,
                collectionName = collectionName,
                title = title,
                dataJson = dataJson
            )
            _selectedCollection.value = collectionName
            showToast("Document saved to collection '$collectionName'.")
        }
    }

    fun deleteDocument(doc: DataDocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(doc.id, doc.projectId, doc.collectionName)
            showToast("Document deleted.")
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
                details = "Deployed updated cloud security rules v2.4."
            )
            showToast("Security rules deployed to live cluster!")
        }
    }

    // Rule Simulator
    fun simulateRule(role: String, operation: String, path: String): RuleSimulationResult {
        return when {
            role == "ADMIN" -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow read, write: if request.auth.role == 'ADMIN'",
                details = "Access Granted: Master Admin has superuser root permissions."
            )
            role == "STAFF" && (path.contains("tournament") || path.contains("match") || path.contains("catalog") || path.contains("channel")) -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow read, write: if request.auth.role in ['ADMIN', 'STAFF']",
                details = "Access Granted: Staff permissions verified for operation."
            )
            role == "STAFF" && (path.contains("vault") || path.contains("secret") || path.contains("payment_key")) -> RuleSimulationResult(
                allowed = false,
                matchedRule = "allow read, write: if request.auth.role == 'ADMIN'",
                details = "Access Denied: Staff token is forbidden from accessing Admin Master Vault."
            )
            role == "USER" && operation == "READ" -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow read: if true;",
                details = "Access Granted: Public read policy active on client collection."
            )
            role == "USER" && operation == "WRITE" && (path.contains("payment") || path.contains("score") || path.contains("chat")) -> RuleSimulationResult(
                allowed = true,
                matchedRule = "allow create: if request.auth != null;",
                details = "Access Granted: Authenticated client allowed to submit record."
            )
            else -> RuleSimulationResult(
                allowed = false,
                matchedRule = "default: deny all;",
                details = "Access Denied: No matching permissive security rule found for $role."
            )
        }
    }

    // Clean & Manage Console ("app সদা করার / কোনো Demo না রাখা")
    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedProjectId.value = ""
            _selectedCollection.value = null
            documentsForSelectedProject.value = emptyList()
            showToast("All data wiped. Zero demo data remains.")
        }
    }

    fun loadStarterTemplates() {
        viewModelScope.launch {
            repository.loadStarterTemplates()
            showToast("Starter app templates loaded.")
        }
    }
}
