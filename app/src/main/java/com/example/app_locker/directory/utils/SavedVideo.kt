package com.example.app_locker.directory.utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_videos")
data class SavedVideo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "video_path")
    val videoPath: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
