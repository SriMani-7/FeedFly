package com.ithoughts.mynaa.tsd.rss.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Feed::class, ArticleItem::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao

    companion object {
        @Volatile
        private lateinit var instance: AppDatabase

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "daily-database"
                    ).build()
                }
            }
            return instance
        }
    }
}