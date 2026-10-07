package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a managed backend project with App Name and Package Name
 */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val appName: String = name,
    val packageName: String = "com.zentrix.app",
    val versionName: String = "1.0.0",
    val description: String = "",
    val category: String = "Mobile Application", // "Esports / Gaming", "Utility", "Games", "Social / Chat", "Media Streaming", "Artificial Intelligence"
    val publishStatus: String = "Development", // "No Play Store (Direct APK)", "Play Store Live", "Launch 2027", "Internal / Alpha"
    val environment: String = "Production", // "Production", "Staging", "Development"
    val adminKey: String, // zx_adm_...
    val staffKey: String, // zx_stf_...
    val clientPublicKey: String, // zx_pub_...
    val activeUsersCount: Int = 0,
    val apiRequestsToday: Int = 0,
    val securityRules: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity for storing Admin Secret Vault keys (ImgBB, Payment Gateways, Webhook, AI tokens)
 */
@Entity(tableName = "vault_secrets")
data class VaultSecretEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String, // "global" or specific project id
    val keyName: String, // e.g. IMGBB_API_KEY, BKASH_APP_KEY, NAGAD_PRIVATE_KEY
    val secretValue: String,
    val category: String, // "Image Upload (ImgBB)", "Payment Gateway", "AI Service", "Cloud Storage", "Custom"
    val isEncrypted: Boolean = true,
    val description: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Entity for project Auth users (Gmail, Email/Password, Phone OTP)
 */
@Entity(tableName = "auth_users")
data class AuthUserEntity(
    @PrimaryKey val uid: String,
    val projectId: String,
    val email: String,
    val phoneNumber: String = "",
    val displayName: String,
    val role: String, // "ADMIN", "STAFF", "MODERATOR", "USER"
    val provider: String, // "gmail", "email_password", "phone_otp"
    val status: String = "ACTIVE", // "ACTIVE", "BLOCKED", "SUSPENDED"
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

/**
 * Entity for Firestore / Supabase style collections and documents
 */
@Entity(tableName = "data_documents")
data class DataDocumentEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val collectionName: String, // e.g. tournaments, calc_history, games_leaderboard, chat_channels
    val title: String,
    val dataJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Entity for security and audit trail logs
 */
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val action: String, // "KEY_GENERATION", "SECRET_ACCESSED", "AUTH_LOGIN", "IMGBB_UPLOAD", "PAYMENT_HOOK"
    val actor: String, // "Master Admin", "Staff Ref", "SDK Client"
    val details: String,
    val status: String = "SUCCESS", // "SUCCESS", "WARN", "DENIED"
    val timestamp: Long = System.currentTimeMillis()
)
