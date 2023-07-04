package com.example.app_locker.directory.utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_images")
data class SavedImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "image_path") val imagePath: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)
