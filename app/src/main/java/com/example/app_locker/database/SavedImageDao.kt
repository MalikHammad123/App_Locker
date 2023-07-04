package com.example.app_locker.database

import androidx.room.*
import com.example.app_locker.directory.utils.SavedImage

@Dao
interface SavedImageDao {
    @Insert
    suspend fun insertImage(savedImage: SavedImage)

    @Query("SELECT * FROM saved_images")
    suspend fun getAllImages(): List<SavedImage>
}
