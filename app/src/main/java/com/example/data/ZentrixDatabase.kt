package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ZentrixDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.AuthUserEntity
import com.example.data.model.DataDocumentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.VaultSecretEntity

@Database(
    entities = [
        ProjectEntity::class,
        VaultSecretEntity::class,
        AuthUserEntity::class,
        DataDocumentEntity::class,
        AuditLogEntity::class
    ],
    version = 2,
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
