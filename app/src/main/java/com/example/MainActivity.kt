package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ConsoleTab
import com.example.ui.ZentrixViewModel
import com.example.ui.components.*
import com.example.ui.theme.ConsoleBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.*

class MainActivity : ComponentActivity() {

    private val viewModel: ZentrixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ZentrixConsoleApp(viewModel)
            }
        }
    }
}

@Composable
fun ZentrixConsoleApp(viewModel: ZentrixViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
    val secrets by viewModel.vaultSecrets.collectAsStateWithLifecycle()
    val allGlobalSecrets by viewModel.allGlobalSecrets.collectAsStateWithLifecycle()
    val revealedSecrets by viewModel.revealedSecrets.collectAsStateWithLifecycle()
    val authUsers by viewModel.authUsers.collectAsStateWithLifecycle()
    val allGlobalUsers by viewModel.allGlobalUsers.collectAsStateWithLifecycle()
    val documents by viewModel.documentsForSelectedProject.collectAsStateWithLifecycle()
    val allDocumentsAcrossProjects by viewModel.allDocumentsAcrossProjects.collectAsStateWithLifecycle()
    val selectedCollection by viewModel.selectedCollection.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val showCreateProjectDialog by viewModel.showCreateProjectDialog.collectAsStateWithLifecycle()
    val projectToEdit by viewModel.projectToEdit.collectAsStateWithLifecycle()
    val showAddSecretDialog by viewModel.showAddSecretDialog.collectAsStateWithLifecycle()
    val showAddUserDialog by viewModel.showAddUserDialog.collectAsStateWithLifecycle()
    val showAddDocDialog by viewModel.showAddDocDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Handle back button: return to PROJECTS tab if currently on secondary tab
    BackHandler(enabled = currentTab != ConsoleTab.PROJECTS) {
        viewModel.setTab(ConsoleTab.PROJECTS)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ConsoleBackground),
        containerColor = ConsoleBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopConsoleBar(
                activeProject = activeProject,
                allProjects = allProjects,
                onSelectProject = { viewModel.selectProject(it) },
                onCreateNewProjectClick = { viewModel.showCreateProjectDialog.value = true }
            )
        },
        bottomBar = {
            NavigationConsoleBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ConsoleTab.PROJECTS -> {
                    ProjectsView(
                        projects = allProjects,
                        activeProjectId = activeProject?.id ?: "",
                        onSelectProject = { viewModel.selectProject(it) },
                        onNavigateTab = { viewModel.setTab(it) },
                        onCreateProjectClick = { viewModel.showCreateProjectDialog.value = true },
                        onEditProjectClick = { viewModel.projectToEdit.value = it },
                        onDeleteProject = { viewModel.deleteProject(it) }
                    )
                }

                ConsoleTab.SDK_CONNECT -> {
                    SdkConfigView(
                        project = activeProject,
                        onRotateKey = { projId, keyType -> viewModel.rotateKey(projId, keyType) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.VAULT -> {
                    SecretVaultView(
                        secrets = secrets,
                        activeProjectId = activeProject?.id ?: "",
                        revealedMap = revealedSecrets,
                        onToggleVisibility = { viewModel.toggleSecretVisibility(it) },
                        onAddSecretClick = { viewModel.showAddSecretDialog.value = true },
                        onDeleteSecret = { viewModel.deleteSecret(it) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.AUTH -> {
                    UsersAuthView(
                        users = authUsers,
                        activeProjectId = activeProject?.id ?: "",
                        onAddUserClick = { viewModel.showAddUserDialog.value = true },
                        onUpdateRole = { uid, role -> viewModel.updateUserRole(uid, role) },
                        onToggleBan = { viewModel.toggleUserBan(it) },
                        onBulkBlock = { viewModel.bulkBlockUsers(it) },
                        onBulkUnblock = { viewModel.bulkUnblockUsers(it) },
                        onBulkDelete = { viewModel.bulkDeleteUsers(it) },
                        onDeleteUser = { viewModel.deleteUser(it) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.DATABASE -> {
                    DatabaseView(
                        documents = documents,
                        activeProjectId = activeProject?.id ?: "",
                        selectedCollection = selectedCollection,
                        onSelectCollection = { viewModel.selectCollection(it) },
                        onAddDocumentClick = { viewModel.showAddDocDialog.value = true },
                        onDeleteDocument = { viewModel.deleteDocument(it) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.SECURITY -> {
                    SecurityRulesView(
                        project = activeProject,
                        onSaveRules = { viewModel.updateSecurityRules(it) },
                        onSimulate = { role, op, path -> viewModel.simulateRule(role, op, path) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.DATA_EXPLORER -> {
                    DataExplorerView(
                        projects = allProjects,
                        secrets = allGlobalSecrets,
                        users = allGlobalUsers,
                        documents = allDocumentsAcrossProjects,
                        logs = auditLogs,
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.ANALYTICS -> {
                    AnalyticsLogsView(
                        logs = auditLogs,
                        activeProjectId = activeProject?.id ?: "",
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.SETTINGS -> {
                    ConsoleSettingsView(
                        totalProjectsCount = allProjects.size,
                        totalSecretsCount = allGlobalSecrets.size,
                        totalUsersCount = allGlobalUsers.size,
                        totalDocsCount = allDocumentsAcrossProjects.size,
                        onClearAllData = { viewModel.clearAllData() },
                        onLoadStarterTemplates = { viewModel.loadStarterTemplates() },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showCreateProjectDialog) {
        CreateProjectDialog(
            onDismiss = { viewModel.showCreateProjectDialog.value = false },
            onCreate = { name, appName, packageName, versionName, desc, cat, status, env ->
                viewModel.createProject(name, appName, packageName, versionName, desc, cat, status, env)
            }
        )
    }

    projectToEdit?.let { project ->
        EditProjectDialog(
            project = project,
            onDismiss = { viewModel.projectToEdit.value = null },
            onSave = { updated ->
                viewModel.updateProject(updated)
            }
        )
    }

    if (showAddSecretDialog) {
        AddSecretDialog(
            activeProjectId = activeProject?.id ?: "",
            onDismiss = { viewModel.showAddSecretDialog.value = false },
            onAdd = { key, value, cat, desc, isGlobal ->
                viewModel.addSecret(key, value, cat, desc, isGlobal)
            }
        )
    }

    if (showAddUserDialog) {
        AddUserDialog(
            activeProjectId = activeProject?.id ?: "",
            onDismiss = { viewModel.showAddUserDialog.value = false },
            onAdd = { email, phone, name, role, provider ->
                viewModel.addUser(email, phone, name, role, provider)
            }
        )
    }

    if (showAddDocDialog) {
        AddDocumentDialog(
            activeProjectId = activeProject?.id ?: "",
            initialCollection = selectedCollection,
            onDismiss = { viewModel.showAddDocDialog.value = false },
            onAdd = { col, title, json ->
                viewModel.addDocument(col, title, json)
            }
        )
    }
}
