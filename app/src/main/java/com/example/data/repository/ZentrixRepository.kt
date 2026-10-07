package com.example.data.repository

import com.example.data.InitialDataProvider
import com.example.data.dao.ZentrixDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ZentrixRepository(private val dao: ZentrixDao) {

    // Projects
    fun getAllProjects(): Flow<List<ProjectEntity>> = dao.getAllProjects()
    fun getProjectById(id: String): Flow<ProjectEntity?> = dao.getProjectById(id)

    suspend fun createProject(
        name: String,
        appName: String,
        packageName: String,
        versionName: String = "1.0.0",
        description: String,
        category: String,
        publishStatus: String,
        environment: String = "Production"
    ): ProjectEntity {
        val cleanSlug = name.lowercase().replace("\\s+".toRegex(), "-").filter { it.isLetterOrDigit() || it == '-' }
        val id = if (cleanSlug.isNotBlank()) "zentrix-$cleanSlug" else "zentrix-${UUID.randomUUID().toString().take(6)}"

        val adminKey = generateKey("zx_sec_adm")
        val staffKey = generateKey("zx_stf_mod")
        val clientKey = generateKey("zx_pub_live")

        val newProject = ProjectEntity(
            id = id,
            name = name,
            appName = if (appName.isNotBlank()) appName else name,
            packageName = if (packageName.isNotBlank()) packageName else "com.zentrix.${cleanSlug.ifBlank { "app" }}",
            versionName = versionName,
            description = description,
            category = category,
            publishStatus = publishStatus,
            environment = environment,
            adminKey = adminKey,
            staffKey = staffKey,
            clientPublicKey = clientKey,
            activeUsersCount = 1,
            apiRequestsToday = 0,
            securityRules = """// Zentrix Security Rules for $name ($packageName)
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth.role in ['ADMIN', 'STAFF'];
    }
  }
}""".trimIndent()
        )
        dao.insertProject(newProject)
        dao.insertLog(
            AuditLogEntity(
                projectId = id,
                action = "PROJECT_CREATED",
                actor = "Master Admin",
                details = "Provisioned project '$name' with package '$packageName' and 3-tier access keys."
            )
        )
        return newProject
    }

    suspend fun updateProject(project: ProjectEntity) = dao.updateProject(project)
    suspend fun deleteProject(id: String) = dao.deleteProject(id)

    suspend fun regenerateProjectKey(projectId: String, keyType: KeyType): String {
        val newKey = when (keyType) {
            KeyType.ADMIN -> generateKey("zx_sec_adm")
            KeyType.STAFF -> generateKey("zx_stf_mod")
            KeyType.CLIENT -> generateKey("zx_pub_live")
        }
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "KEY_ROTATED",
                actor = "Master Admin",
                details = "Rotated ${keyType.name} security key."
            )
        )
        return newKey
    }

    // Vault Secrets
    fun getAllSecrets(): Flow<List<VaultSecretEntity>> = dao.getAllSecrets()
    fun getSecretsForProject(projectId: String): Flow<List<VaultSecretEntity>> = dao.getSecretsForProject(projectId)

    suspend fun addSecret(
        projectId: String,
        keyName: String,
        secretValue: String,
        category: String,
        description: String
    ) {
        val secret = VaultSecretEntity(
            projectId = projectId,
            keyName = keyName.trim().uppercase().replace(" ", "_"),
            secretValue = secretValue.trim(),
            category = category,
            description = description
        )
        dao.insertSecret(secret)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "SECRET_STORED",
                actor = "Master Admin",
                details = "Saved vault secret '$keyName' under category '$category'."
            )
        )
    }

    suspend fun deleteSecret(id: Long, keyName: String, projectId: String) {
        dao.deleteSecret(id)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "SECRET_REVOKED",
                actor = "Master Admin",
                details = "Revoked vault secret '$keyName'."
            )
        )
    }

    // Auth Users
    fun getUsersForProject(projectId: String): Flow<List<AuthUserEntity>> = dao.getUsersForProject(projectId)
    fun getAllUsers(): Flow<List<AuthUserEntity>> = dao.getAllUsers()

    suspend fun createUser(
        projectId: String,
        email: String,
        phoneNumber: String = "",
        displayName: String,
        role: String,
        provider: String
    ) {
        val uid = "usr_${role.lowercase()}_${UUID.randomUUID().toString().take(6)}"
        val user = AuthUserEntity(
            uid = uid,
            projectId = projectId,
            email = email,
            phoneNumber = phoneNumber,
            displayName = displayName,
            role = role,
            provider = provider,
            status = "ACTIVE"
        )
        dao.insertUser(user)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "USER_CREATED",
                actor = "Master Admin",
                details = "Registered account for '$email' (phone: '$phoneNumber') with role $role."
            )
        )
    }

    suspend fun updateUserRole(uid: String, projectId: String, newRole: String) {
        dao.updateUserRole(uid, newRole)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "ROLE_UPDATED",
                actor = "Master Admin",
                details = "Changed user $uid role to $newRole."
            )
        )
    }

    suspend fun toggleUserBan(user: AuthUserEntity) {
        val newStatus = if (user.status == "BLOCKED" || user.status == "BANNED") "ACTIVE" else "BLOCKED"
        dao.updateUserStatus(user.uid, newStatus)
        dao.insertLog(
            AuditLogEntity(
                projectId = user.projectId,
                action = if (newStatus == "BLOCKED") "USER_BLOCKED" else "USER_UNBLOCKED",
                actor = "Master Admin",
                details = "User ${user.email} status set to $newStatus."
            )
        )
    }

    suspend fun bulkBlockUsers(uids: List<String>, projectId: String) {
        if (uids.isEmpty()) return
        dao.bulkUpdateUserStatus(uids, "BLOCKED")
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "BULK_USERS_BLOCKED",
                actor = "Master Admin",
                details = "Bulk blocked ${uids.size} user accounts."
            )
        )
    }

    suspend fun bulkUnblockUsers(uids: List<String>, projectId: String) {
        if (uids.isEmpty()) return
        dao.bulkUpdateUserStatus(uids, "ACTIVE")
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "BULK_USERS_UNBLOCKED",
                actor = "Master Admin",
                details = "Bulk unblocked ${uids.size} user accounts."
            )
        )
    }

    suspend fun bulkDeleteUsers(uids: List<String>, projectId: String) {
        if (uids.isEmpty()) return
        dao.bulkDeleteUsers(uids)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "BULK_USERS_DELETED",
                actor = "Master Admin",
                details = "Permanently removed ${uids.size} user accounts."
            )
        )
    }

    suspend fun deleteUser(uid: String, projectId: String) {
        dao.deleteUser(uid)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "USER_DELETED",
                actor = "Master Admin",
                details = "Removed user account $uid."
            )
        )
    }

    // Documents & Collections
    fun getCollectionNames(projectId: String): Flow<List<String>> = dao.getCollectionNames(projectId)
    fun getDocuments(projectId: String, collectionName: String): Flow<List<DataDocumentEntity>> =
        dao.getDocuments(projectId, collectionName)
    fun getAllDocumentsForProject(projectId: String): Flow<List<DataDocumentEntity>> =
        dao.getAllDocumentsForProject(projectId)
    fun getAllDocuments(): Flow<List<DataDocumentEntity>> = dao.getAllDocuments()

    suspend fun addDocument(
        projectId: String,
        collectionName: String,
        title: String,
        dataJson: String
    ) {
        val id = "doc_${collectionName.take(4)}_${UUID.randomUUID().toString().take(8)}"
        val doc = DataDocumentEntity(
            id = id,
            projectId = projectId,
            collectionName = collectionName.lowercase().trim(),
            title = title,
            dataJson = dataJson
        )
        dao.insertDocument(doc)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "DOCUMENT_WRITTEN",
                actor = "Admin Console",
                details = "Stored document $id in collection '$collectionName'."
            )
        )
    }

    suspend fun deleteDocument(id: String, projectId: String, collectionName: String) {
        dao.deleteDocument(id)
        dao.insertLog(
            AuditLogEntity(
                projectId = projectId,
                action = "DOCUMENT_DELETED",
                actor = "Admin Console",
                details = "Deleted document $id from '$collectionName'."
            )
        )
    }

    // Audit Logs
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    fun getLogsForProject(projectId: String): Flow<List<AuditLogEntity>> = dao.getLogsForProject(projectId)

    suspend fun recordLog(projectId: String, action: String, actor: String, details: String) {
        dao.insertLog(AuditLogEntity(projectId = projectId, action = action, actor = actor, details = details))
    }

    // Database Reset & Templates
    suspend fun clearAllData() {
        InitialDataProvider.clearAllData(dao)
    }

    suspend fun loadStarterTemplates() {
        InitialDataProvider.loadStarterTemplates(dao)
    }

    private fun generateKey(prefix: String): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return "${prefix}_${uuid.take(16)}"
    }
}

enum class KeyType { ADMIN, STAFF, CLIENT }
