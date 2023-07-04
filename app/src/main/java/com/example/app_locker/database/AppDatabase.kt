package com.example.app_locker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app_locker.directory.utils.SavedImage
import com.example.app_locker.directory.utils.SavedVideo
@Database(entities = [SavedImage::class, SavedVideo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedImageDao(): SavedImageDao
    abstract fun savedVideoDao(): SavedVideoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "your_database_name"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
