package com.example

import android.os.Bundle
import android.widget.Toast
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
    val revealedSecrets by viewModel.revealedSecrets.collectAsStateWithLifecycle()
    val authUsers by viewModel.authUsers.collectAsStateWithLifecycle()
    val documents by viewModel.documentsForSelectedProject.collectAsStateWithLifecycle()
    val selectedCollection by viewModel.selectedCollection.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val showCreateProjectDialog by viewModel.showCreateProjectDialog.collectAsStateWithLifecycle()
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

    // Handle back button: return to PROJECTS tab if currently on sub-tab
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
                        activeProjectId = activeProject?.id ?: "zentrix-esport",
                        onSelectProject = { viewModel.selectProject(it) },
                        onNavigateTab = { viewModel.setTab(it) },
                        onCreateProjectClick = { viewModel.showCreateProjectDialog.value = true },
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
                        activeProjectId = activeProject?.id ?: "zentrix-esport",
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
                        activeProjectId = activeProject?.id ?: "zentrix-esport",
                        onAddUserClick = { viewModel.showAddUserDialog.value = true },
                        onUpdateRole = { uid, role -> viewModel.updateUserRole(uid, role) },
                        onToggleBan = { viewModel.toggleUserBan(it) },
                        onDeleteUser = { viewModel.deleteUser(it) },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                ConsoleTab.DATABASE -> {
                    DatabaseView(
                        documents = documents,
                        activeProjectId = activeProject?.id ?: "zentrix-esport",
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

                ConsoleTab.ANALYTICS -> {
                    AnalyticsLogsView(
                        logs = auditLogs,
                        activeProjectId = activeProject?.id ?: "zentrix-esport",
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
            onCreate = { name, bnName, desc, cat, status, env ->
                viewModel.createProject(name, bnName, desc, cat, status, env)
            }
        )
    }

    if (showAddSecretDialog) {
        AddSecretDialog(
            activeProjectId = activeProject?.id ?: "zentrix-esport",
            onDismiss = { viewModel.showAddSecretDialog.value = false },
            onAdd = { key, value, cat, desc, isGlobal ->
                viewModel.addSecret(key, value, cat, desc, isGlobal)
            }
        )
    }

    if (showAddUserDialog) {
        AddUserDialog(
            activeProjectId = activeProject?.id ?: "zentrix-esport",
            onDismiss = { viewModel.showAddUserDialog.value = false },
            onAdd = { email, name, role, provider ->
                viewModel.addUser(email, name, role, provider)
            }
        )
    }

    if (showAddDocDialog) {
        AddDocumentDialog(
            activeProjectId = activeProject?.id ?: "zentrix-esport",
            initialCollection = selectedCollection,
            onDismiss = { viewModel.showAddDocDialog.value = false },
            onAdd = { col, title, json ->
                viewModel.addDocument(col, title, json)
            }
        )
    }
}
