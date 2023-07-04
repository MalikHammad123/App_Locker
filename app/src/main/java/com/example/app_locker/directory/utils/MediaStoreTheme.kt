package com.example.app_locker.directory.utils

import android.net.Uri
import java.util.*

data class MediaStoreTheme(
    val id: Long,
    val displayName: String,
    val dateAdded: Date,
    val contentUri: Uri,
    val path: String
)
