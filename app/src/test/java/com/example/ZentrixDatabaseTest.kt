package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ZentrixDatabase
import com.example.data.repository.ZentrixRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ZentrixDatabaseTest {

    private lateinit var db: ZentrixDatabase
    private lateinit var repository: ZentrixRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ZentrixDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ZentrixRepository(db.zentrixDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testEmptyOnFreshStart() = runBlocking {
        // Zero demo data test
        val projects = repository.getAllProjects().first()
        assertTrue("Initially empty on fresh start with zero demo data", projects.isEmpty())
    }

    @Test
    fun testCreateCustomProjectWithPackageName() = runBlocking {
        val proj = repository.createProject(
            name = "Zentrix Custom App",
            appName = "Zentrix Esport Tournament",
            packageName = "com.zentrix.esport",
            versionName = "1.0.0",
            description = "Custom testing tournament project",
            category = "Esports / Gaming",
            publishStatus = "Direct APK"
        )
        assertNotNull(proj)
        assertEquals("Zentrix Esport Tournament", proj.appName)
        assertEquals("com.zentrix.esport", proj.packageName)
        assertTrue(proj.adminKey.startsWith("zx_sec_adm"))
        assertTrue(proj.staffKey.startsWith("zx_stf_mod"))
        assertTrue(proj.clientPublicKey.startsWith("zx_pub_live"))
    }

    @Test
    fun testLoadTemplatesAndClearAll() = runBlocking {
        repository.loadStarterTemplates()
        val projects = repository.getAllProjects().first()
        assertEquals(2, projects.size)

        // Wipe all data
        repository.clearAllData()
        val afterWipe = repository.getAllProjects().first()
        assertTrue(afterWipe.isEmpty())
    }

    @Test
    fun testUserWithPhoneAndBulkBlock() = runBlocking {
        repository.createUser(
            projectId = "test-project",
            email = "player1@zentrix.io",
            phoneNumber = "+8801712345678",
            displayName = "Player One",
            role = "USER",
            provider = "gmail"
        )
        repository.createUser(
            projectId = "test-project",
            email = "player2@zentrix.io",
            phoneNumber = "+8801899887766",
            displayName = "Player Two",
            role = "USER",
            provider = "phone_otp"
        )

        val users = repository.getUsersForProject("test-project").first()
        assertEquals(2, users.size)
        assertTrue(users.any { it.phoneNumber == "+8801712345678" })

        // Test bulk block
        val uids = users.map { it.uid }
        repository.bulkBlockUsers(uids, "test-project")
        val blockedUsers = repository.getUsersForProject("test-project").first()
        assertTrue(blockedUsers.all { it.status == "BLOCKED" })

        // Test bulk unblock
        repository.bulkUnblockUsers(uids, "test-project")
        val unblockedUsers = repository.getUsersForProject("test-project").first()
        assertTrue(unblockedUsers.all { it.status == "ACTIVE" })
    }
}
