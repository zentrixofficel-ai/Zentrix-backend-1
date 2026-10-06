package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ZentrixDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProjectEntity::class,
        VaultSecretEntity::class,
        AuthUserEntity::class,
        DataDocumentEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZentrixDatabase : RoomDatabase() {

    abstract fun zentrixDao(): ZentrixDao

    companion object {
        @Volatile
        private var INSTANCE: ZentrixDatabase? = null

        fun getDatabase(context: Context): ZentrixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZentrixDatabase::class.java,
                    "zentrix_cloud_backend.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        InitialDataProvider.populateInitialData(database.zentrixDao())
                    }
                }
            }
        }
    }
}
