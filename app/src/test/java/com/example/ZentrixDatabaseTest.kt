package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.InitialDataProvider
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
@Config(sdk = [36])
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
    fun testInitialProjectsSeeded() = runBlocking {
        repository.ensureDataPopulated()
        val projects = repository.getAllProjects().first()
        assertTrue("Projects should be seeded", projects.isNotEmpty())
        assertTrue("Should include Free Fire Esports project", projects.any { it.id == "zentrix-esport" })
        assertTrue("Should include Calculator project", projects.any { it.id == "zentrix-calc" })
        assertTrue("Should include AI project", projects.any { it.id == "zentrix-ai" })
    }

    @Test
    fun testSecretsSeeded() = runBlocking {
        repository.ensureDataPopulated()
        val secrets = repository.getAllSecrets().first()
        assertTrue("Secrets should include ImgBB key", secrets.any { it.keyName == "IMGBB_API_KEY" })
        assertTrue("Secrets should include bKash app key", secrets.any { it.keyName == "BKASH_MERCHANT_APP_KEY" })
    }

    @Test
    fun testCreateCustomProject() = runBlocking {
        val proj = repository.createProject(
            name = "Zentrix Custom App",
            bnName = "কাস্টম অ্যাপ",
            description = "Custom testing project",
            category = "Games",
            publishStatus = "Play Store Live"
        )
        assertNotNull(proj)
        assertTrue(proj.adminKey.startsWith("zx_sec_adm"))
        assertTrue(proj.staffKey.startsWith("zx_stf_mod"))
        assertTrue(proj.clientPublicKey.startsWith("zx_pub_live"))
    }
}
