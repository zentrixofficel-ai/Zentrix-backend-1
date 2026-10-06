package com.example.data

import com.example.data.dao.ZentrixDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity
import kotlinx.coroutines.flow.firstOrNull

object InitialDataProvider {

    suspend fun populateInitialData(dao: ZentrixDao) {
        val existing = dao.getAllProjects().firstOrNull()
        if (!existing.isNullOrEmpty()) return

        // 1. Zentrix Esports (Free Fire Tournament)
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-esport",
                name = "Zentrix Esports",
                bnName = "ফ্রি ফায়ার টুর্নামেন্ট হাব",
                description = "Free Fire tournament hosting, match scheduling, automated room IDs, prize pools & team registrations.",
                category = "Esports / Gaming",
                publishStatus = "No Play Store (Direct APK)",
                environment = "Production",
                adminKey = "zx_sec_adm_live_94f810aa72bc304d",
                staffKey = "zx_stf_mod_live_21e7841cbb5920ea",
                clientPublicKey = "zx_pub_live_7a39e802b115ff68",
                activeUsersCount = 14250,
                apiRequestsToday = 48920,
                securityRules = """// Zentrix Security Rules - Esports Hub
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    // Public tournament schedules & leaderboard
    match /tournaments/{matchId} {
      allow read: if true;
      allow write: if request.auth.role in ['ADMIN', 'STAFF'];
    }
    // Match room password & slot allocations
    match /match_rooms/{roomId} {
      allow read: if request.auth != null && request.auth.isRegistered;
      allow write: if request.auth.role in ['ADMIN', 'STAFF'];
    }
    // Tournament entry fees & bkash/nagad receipts
    match /payments/{paymentId} {
      allow create: if request.auth != null;
      allow read, update: if request.auth.role == 'ADMIN';
    }
  }
}""".trimIndent()
            )
        )

        // 2. Zentrix Calculator
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-calc",
                name = "Zentrix Calculator",
                bnName = "জেনট্রিক্স ক্যালকুলেটর",
                description = "Cloud-synced smart scientific calculator, financial history, formula vault & offline cached math engine.",
                category = "Utility",
                publishStatus = "Play Store Live",
                environment = "Production",
                adminKey = "zx_sec_adm_live_55ab4011cc8941da",
                staffKey = "zx_stf_mod_live_33bc7188aa12409f",
                clientPublicKey = "zx_pub_live_99fa1240cc88910e",
                activeUsersCount = 8620,
                apiRequestsToday = 19430,
                securityRules = """// Zentrix Security Rules - Calculator
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    match /users/{userId}/calc_history/{entryId} {
      allow read, write: if request.auth.uid == userId;
    }
    match /presets/{presetId} {
      allow read: if true;
      allow write: if request.auth.role == 'ADMIN';
    }
  }
}""".trimIndent()
            )
        )

        // 3. Zentrix Games (Arcade & Mini Games)
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-games",
                name = "Zentrix Games 🎮",
                bnName = "বিভিন্ন গেম হাব",
                description = "Arcade games, multiplayer matchmaking, real-time leaderboard scores & in-game virtual coin wallet.",
                category = "Games",
                publishStatus = "Play Store Live",
                environment = "Production",
                adminKey = "zx_sec_adm_live_8811ee40a83152bb",
                staffKey = "zx_stf_mod_live_6622ff33aa94819d",
                clientPublicKey = "zx_pub_live_5544cc22ee71830a",
                activeUsersCount = 22400,
                apiRequestsToday = 96450,
                securityRules = """// Zentrix Security Rules - Games Arcade
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    match /leaderboard/{scoreId} {
      allow read: if true;
      allow create: if request.auth != null && request.resource.score < 999999;
    }
    match /inventory/{item} {
      allow read: if request.auth != null;
      allow write: if request.auth.role in ['ADMIN', 'STAFF'];
    }
  }
}""".trimIndent()
            )
        )

        // 4. Zentrix Connect (WhatsApp / Telegram alternative)
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-connect",
                name = "Zentrix Connect",
                bnName = "চ্যাট ও মেসেঞ্জার (WhatsApp/Telegram এর মতো)",
                description = "Encrypted messaging, channels, voice notes, stickers & group conference calls.",
                category = "Social / Chat",
                publishStatus = "Launch 2027",
                environment = "Staging",
                adminKey = "zx_sec_adm_stg_4188bb00aa7721cc",
                staffKey = "zx_stf_mod_stg_1922dd44ee8812bb",
                clientPublicKey = "zx_pub_stg_7733aa99cc4410ee",
                activeUsersCount = 1250,
                apiRequestsToday = 8900,
                securityRules = """// Zentrix Security Rules - Encrypted Messenger
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    match /channels/{channelId}/messages/{msgId} {
      allow read: if request.auth.uid in resource.data.members;
      allow create: if request.auth.uid in resource.data.members;
    }
    match /system_announcements/{id} {
      allow read: if true;
      allow write: if request.auth.role == 'ADMIN';
    }
  }
}""".trimIndent()
            )
        )

        // 5. Zentrix Stream (Video streaming & downloader)
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-stream",
                name = "Zentrix Stream",
                bnName = "ভিডিও দেখা এবং ডাউনলোড",
                description = "Ultra HD video streaming, multi-res offline downloader, CDN caching & media library management.",
                category = "Media Streaming",
                publishStatus = "Launch 2027",
                environment = "Development",
                adminKey = "zx_sec_adm_dev_6633dd11bb8834aa",
                staffKey = "zx_stf_mod_dev_4411bb22cc9941ee",
                clientPublicKey = "zx_pub_dev_3388ff55aa1192cc",
                activeUsersCount = 480,
                apiRequestsToday = 3120,
                securityRules = """// Zentrix Security Rules - Video Stream
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    match /catalog/{videoId} {
      allow read: if true;
      allow write: if request.auth.role in ['ADMIN', 'STAFF'];
    }
    match /download_tokens/{tokenId} {
      allow read, create: if request.auth != null;
    }
  }
}""".trimIndent()
            )
        )

        // 6. Zentrix AI
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-ai",
                name = "Zentrix AI",
                bnName = "জেনট্রিক্স এআই অ্যাপ",
                description = "AI chatbot backend, vision analysis, prompt engineering workspace & Gemini token routing engine.",
                category = "Artificial Intelligence",
                publishStatus = "Play Store Live",
                environment = "Production",
                adminKey = "zx_sec_adm_live_1100aa88bb2233dd",
                staffKey = "zx_stf_mod_live_9933cc44dd1122ee",
                clientPublicKey = "zx_pub_live_4477bb33aa8822ff",
                activeUsersCount = 31800,
                apiRequestsToday = 142800,
                securityRules = """// Zentrix Security Rules - AI Hub
rules_version = '2';
service zentrix.cloud {
  match /databases/{database}/documents {
    match /prompts/{promptId} {
      allow read, write: if request.auth.uid == resource.data.ownerId;
    }
    match /model_quotas/{userId} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.role == 'ADMIN';
    }
  }
}""".trimIndent()
            )
        )

        // Seed Admin Vault Secrets (ImgBB, Payment Gateways, AI keys)
        val secrets = listOf(
            VaultSecretEntity(
                projectId = "global",
                keyName = "IMGBB_API_KEY",
                secretValue = "9f8e4c76a10d92b8478f72eb3309a144",
                category = "Image Upload (ImgBB)",
                description = "Used by all client apps to upload player avatars, tournament screenshots, and media thumbnails to ImgBB CDN."
            ),
            VaultSecretEntity(
                projectId = "zentrix-esport",
                keyName = "BKASH_MERCHANT_APP_KEY",
                secretValue = "bkash_live_app_key_8849201948301",
                category = "Payment Gateway",
                description = "bKash checkout API key for Free Fire tournament entry fees collection."
            ),
            VaultSecretEntity(
                projectId = "zentrix-esport",
                keyName = "BKASH_APP_SECRET_TOKEN",
                secretValue = "bks_sec_live_994827103857109284718",
                category = "Payment Gateway",
                description = "bKash grant token authorization signature."
            ),
            VaultSecretEntity(
                projectId = "zentrix-esport",
                keyName = "NAGAD_MERCHANT_PRIVATE_KEY",
                secretValue = "ngd_mch_8291048201994819_live_priv",
                category = "Payment Gateway",
                description = "Nagad PGW private key for instant prize money disbursement to winners."
            ),
            VaultSecretEntity(
                projectId = "zentrix-ai",
                keyName = "GEMINI_PRO_API_KEY",
                secretValue = "AIzaSyB_zx794019284019248a8f1023",
                category = "AI Service",
                description = "Google Gemini 2.5 Flash token for Zentrix AI assistant backend."
            ),
            VaultSecretEntity(
                projectId = "global",
                keyName = "PAYMENT_WEBHOOK_SECRET",
                secretValue = "whsec_zentrix_99182a47291049bc88",
                category = "Webhook",
                description = "Secret used to verify incoming webhook signatures from payment providers."
            ),
            VaultSecretEntity(
                projectId = "zentrix-connect",
                keyName = "CHAT_ENCRYPTION_SALT",
                secretValue = "salt_zx_aes256_gcm_99382103847192",
                category = "Security",
                description = "End-to-End master salt for client message integrity."
            )
        )
        secrets.forEach { dao.insertSecret(it) }

        // Seed Auth Users (Gmail, Email, Staff, Admin)
        val users = listOf(
            AuthUserEntity(
                uid = "usr_adm_001",
                projectId = "zentrix-esport",
                email = "notebook83319@gmail.com",
                displayName = "Zentrix Founder (Super Admin)",
                role = "ADMIN",
                provider = "gmail",
                status = "ACTIVE"
            ),
            AuthUserEntity(
                uid = "usr_stf_002",
                projectId = "zentrix-esport",
                email = "referee.ff@zentrix.io",
                displayName = "Tournament Head Staff (Referee)",
                role = "STAFF",
                provider = "email_password",
                status = "ACTIVE"
            ),
            AuthUserEntity(
                uid = "usr_stf_003",
                projectId = "zentrix-esport",
                email = "support@zentrixesports.com",
                displayName = "Moderator & Slot Coordinator",
                role = "STAFF",
                provider = "email_password",
                status = "ACTIVE"
            ),
            AuthUserEntity(
                uid = "usr_ply_004",
                projectId = "zentrix-esport",
                email = "pro_gamer_ff99@gmail.com",
                displayName = "Shakib FreeFire (Clan Leader)",
                role = "USER",
                provider = "gmail",
                status = "ACTIVE"
            ),
            AuthUserEntity(
                uid = "usr_adm_005",
                projectId = "zentrix-calc",
                email = "notebook83319@gmail.com",
                displayName = "Admin (Calculator Lead)",
                role = "ADMIN",
                provider = "gmail",
                status = "ACTIVE"
            ),
            AuthUserEntity(
                uid = "usr_adm_006",
                projectId = "zentrix-ai",
                email = "notebook83319@gmail.com",
                displayName = "AI Ops Admin",
                role = "ADMIN",
                provider = "gmail",
                status = "ACTIVE"
            )
        )
        users.forEach { dao.insertUser(it) }

        // Seed Collections & Documents
        val documents = listOf(
            DataDocumentEntity(
                id = "tourn_ff_squad_grand_101",
                projectId = "zentrix-esport",
                collectionName = "tournaments",
                title = "Free Fire Mega Championship Season 4",
                dataJson = """{
  "title": "FF Championship Grand Finale",
  "mode": "Squad (Battle Royale)",
  "slots": 48,
  "registeredTeams": 44,
  "entryFee": "৳ 120 BDT",
  "prizePool": "৳ 10,000 BDT",
  "map": "Bermuda Remastered",
  "status": "LIVE_REGISTRATION",
  "roomId": "FF-9021-ROOM",
  "roomPass": "zx8849"
}"""
            ),
            DataDocumentEntity(
                id = "tourn_ff_duo_flash_102",
                projectId = "zentrix-esport",
                collectionName = "tournaments",
                title = "Daily Fast Cup #29 (Duo)",
                dataJson = """{
  "title": "Daily Duo Clash",
  "mode": "Duo",
  "slots": 24,
  "registeredTeams": 24,
  "entryFee": "৳ 50 BDT",
  "prizePool": "৳ 2,500 BDT",
  "map": "Purgatory",
  "status": "SLOTS_FULL",
  "roomId": "FF-DUO-7711",
  "roomPass": "ready12"
}"""
            ),
            DataDocumentEntity(
                id = "team_alpha_reg_01",
                projectId = "zentrix-esport",
                collectionName = "team_registrations",
                title = "Team Toxic Tigers (Slot 12)",
                dataJson = """{
  "teamName": "Toxic Tigers",
  "captainUid": "usr_ply_004",
  "ffUids": ["884910294", "771920491", "551920391", "991029381"],
  "bkashTrxId": "9KJ882LK91",
  "paymentVerified": true,
  "assignedSlot": 12
}"""
            ),
            DataDocumentEntity(
                id = "calc_preset_fin_01",
                projectId = "zentrix-calc",
                collectionName = "formulas",
                title = "EMI & Compound Interest Calculator",
                dataJson = """{
  "name": "Loan EMI Matrix",
  "formula": "P * r * (1 + r)^n / ((1 + r)^n - 1)",
  "variables": ["P: Principal", "r: Rate", "n: Tenure Months"],
  "syncVersion": "2.4"
}"""
            ),
            DataDocumentEntity(
                id = "game_arcade_lb_top",
                projectId = "zentrix-games",
                collectionName = "leaderboard",
                title = "Speed Runner Global Rankings",
                dataJson = """{
  "game": "Zentrix Speed Runner",
  "season": 5,
  "topPlayers": [
    {"rank": 1, "player": "TanvirX", "score": 98450, "country": "BD"},
    {"rank": 2, "player": "RafiGhost", "score": 95100, "country": "BD"},
    {"rank": 3, "player": "CyberNinja", "score": 92300, "country": "MY"}
  ]
}"""
            ),
            DataDocumentEntity(
                id = "ai_system_model_gemini",
                projectId = "zentrix-ai",
                collectionName = "model_configs",
                title = "Gemini 2.5 Flash Production Endpoint",
                dataJson = """{
  "model": "models/gemini-2.5-flash",
  "maxTokens": 4096,
  "temperature": 0.7,
  "rateLimitPerUserPerMin": 20,
  "activeRoute": "PRIMARY_GCP_CLUSTER"
}"""
            )
        )
        documents.forEach { dao.insertDocument(it) }

        // Seed Audit Logs
        val logs = listOf(
            AuditLogEntity(
                projectId = "zentrix-esport",
                action = "STAFF_KEY_PROVISIONED",
                actor = "Master Admin (notebook83319@gmail.com)",
                details = "Generated new Staff Key for Referee team with slot-management permissions.",
                status = "SUCCESS"
            ),
            AuditLogEntity(
                projectId = "global",
                action = "VAULT_ACCESS",
                actor = "Master Admin",
                details = "Updated IMGBB_API_KEY with high-capacity production quota.",
                status = "SUCCESS"
            ),
            AuditLogEntity(
                projectId = "zentrix-esport",
                action = "PAYMENT_WEBHOOK_RECEIVED",
                actor = "bKash PGW Webhook",
                details = "Trx ID 9KJ882LK91 verified for ৳ 120 BDT. Team slot #12 confirmed.",
                status = "SUCCESS"
            ),
            AuditLogEntity(
                projectId = "zentrix-ai",
                action = "RATE_LIMIT_CHECK",
                actor = "Zentrix AI SDK Client",
                details = "Processed 1,240 concurrent tokens across Asian region nodes.",
                status = "SUCCESS"
            )
        )
        logs.forEach { dao.insertLog(it) }
    }
}
