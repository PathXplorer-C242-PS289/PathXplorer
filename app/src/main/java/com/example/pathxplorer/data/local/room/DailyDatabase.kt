package com.example.pathxplorer.data.local.room

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pathxplorer.data.local.entity.DailyQuestEntity

@Database(entities = [DailyQuestEntity::class], version = 1, exportSchema = false)
abstract class DailyDatabase : RoomDatabase() {

    abstract fun dailyDao(): DailyDao

    companion object {
        @Volatile
        private var INSTANCE: DailyDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): DailyDatabase {

            if (INSTANCE == null) {
                synchronized(DailyDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DailyDatabase::class.java, "favorite_databse"
                    ).build()
                }
            }

            return INSTANCE as DailyDatabase
        }
    }

}