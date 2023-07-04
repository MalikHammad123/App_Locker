package com.example.app_locker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app_locker.directory.utils.SavedVideo

@Dao
interface SavedVideoDao {
    @Insert
    suspend fun insertVideo(video: SavedVideo)

    @Query("SELECT * FROM saved_videos")
    suspend fun getAllVideos(): List<SavedVideo>
}
