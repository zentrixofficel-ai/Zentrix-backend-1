package com.example.data

import com.example.data.dao.ZentrixDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity

object InitialDataProvider {

    /**
     * Completely wipe all demo and saved data for a clean fresh slate
     */
    suspend fun clearAllData(dao: ZentrixDao) {
        dao.clearAllProjects()
        dao.clearAllSecrets()
        dao.clearAllUsers()
        dao.clearAllDocuments()
        dao.clearLogs()
    }

    /**
     * Optional Starter Templates (Only loaded if user explicitly taps "Load Templates" in settings)
     */
    suspend fun loadStarterTemplates(dao: ZentrixDao) {
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-esport",
                name = "Zentrix Esports",
                appName = "Zentrix Esports Free Fire",
                packageName = "com.zentrix.esports",
                versionName = "1.0.0",
                description = "Free Fire Tournament host with slots, match rooms, and prizes.",
                category = "Esports / Gaming",
                publishStatus = "No Play Store (Direct APK)",
                environment = "Production",
                adminKey = "zx_sec_adm_live_94f810aa72bc304d",
                staffKey = "zx_stf_mod_live_21e7841cbb5920ea",
                clientPublicKey = "zx_pub_live_7a39e802b115ff68",
                activeUsersCount = 120,
                apiRequestsToday = 450
            )
        )
        dao.insertProject(
            ProjectEntity(
                id = "zentrix-calc",
                name = "Zentrix Calculator",
                appName = "Zentrix Calculator Pro",
                packageName = "com.zentrix.calculator",
                versionName = "2.1.0",
                description = "Cloud-synced scientific calculator and formula storage.",
                category = "Utility",
                publishStatus = "Play Store Live",
                environment = "Production",
                adminKey = "zx_sec_adm_live_55ab4011cc8941da",
                staffKey = "zx_stf_mod_live_33bc7188aa12409f",
                clientPublicKey = "zx_pub_live_99fa1240cc88910e",
                activeUsersCount = 85,
                apiRequestsToday = 210
            )
        )
    }
}
