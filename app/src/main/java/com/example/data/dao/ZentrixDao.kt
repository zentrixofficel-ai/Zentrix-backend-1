package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZentrixDao {

    // --- Projects ---
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    @Query("DELETE FROM projects")
    suspend fun clearAllProjects()

    // --- Vault Secrets ---
    @Query("SELECT * FROM vault_secrets ORDER BY lastUpdated DESC")
    fun getAllSecrets(): Flow<List<VaultSecretEntity>>

    @Query("SELECT * FROM vault_secrets WHERE projectId = :projectId OR projectId = 'global' ORDER BY lastUpdated DESC")
    fun getSecretsForProject(projectId: String): Flow<List<VaultSecretEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: VaultSecretEntity)

    @Query("DELETE FROM vault_secrets WHERE id = :id")
    suspend fun deleteSecret(id: Long)

    @Query("DELETE FROM vault_secrets")
    suspend fun clearAllSecrets()

    // --- Auth Users ---
    @Query("SELECT * FROM auth_users WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getUsersForProject(projectId: String): Flow<List<AuthUserEntity>>

    @Query("SELECT * FROM auth_users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<AuthUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AuthUserEntity)

    @Query("UPDATE auth_users SET role = :newRole WHERE uid = :uid")
    suspend fun updateUserRole(uid: String, newRole: String)

    @Query("UPDATE auth_users SET status = :newStatus WHERE uid = :uid")
    suspend fun updateUserStatus(uid: String, newStatus: String)

    @Query("UPDATE auth_users SET status = :newStatus WHERE uid IN (:uids)")
    suspend fun bulkUpdateUserStatus(uids: List<String>, newStatus: String)

    @Query("DELETE FROM auth_users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)

    @Query("DELETE FROM auth_users WHERE uid IN (:uids)")
    suspend fun bulkDeleteUsers(uids: List<String>)

    @Query("DELETE FROM auth_users")
    suspend fun clearAllUsers()

    // --- Data Documents / Collections ---
    @Query("SELECT DISTINCT collectionName FROM data_documents WHERE projectId = :projectId")
    fun getCollectionNames(projectId: String): Flow<List<String>>

    @Query("SELECT * FROM data_documents WHERE projectId = :projectId AND collectionName = :collectionName ORDER BY updatedAt DESC")
    fun getDocuments(projectId: String, collectionName: String): Flow<List<DataDocumentEntity>>

    @Query("SELECT * FROM data_documents WHERE projectId = :projectId ORDER BY updatedAt DESC")
    fun getAllDocumentsForProject(projectId: String): Flow<List<DataDocumentEntity>>

    @Query("SELECT * FROM data_documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<DataDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DataDocumentEntity)

    @Query("DELETE FROM data_documents WHERE id = :id")
    suspend fun deleteDocument(id: String)

    @Query("DELETE FROM data_documents")
    suspend fun clearAllDocuments()

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE projectId = :projectId ORDER BY timestamp DESC LIMIT 200")
    fun getLogsForProject(projectId: String): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clearLogs()
}
