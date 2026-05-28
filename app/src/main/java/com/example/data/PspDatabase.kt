package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PspGameEntity::class], version = 1, exportSchema = false)
abstract class PspDatabase : RoomDatabase() {
    abstract fun pspGameDao(): PspGameDao

    companion object {
        @Volatile
        private var INSTANCE: PspDatabase? = null

        fun getDatabase(context: Context): PspDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PspDatabase::class.java,
                    "psp_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
