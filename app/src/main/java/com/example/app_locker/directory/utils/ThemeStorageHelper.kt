package com.example.app_locker.directory.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

object ThemeStorageHelper {
    fun saveThemeToStorage(context: Context, bitmap: Bitmap, folderName: String): String? {
        val filename = "${System.currentTimeMillis()}.jpg"
        var imagePath: String? = null

        try {
            var fos: OutputStream? = null

            val folderDir = File(context.filesDir, folderName)
            if (!folderDir.exists()) {
                folderDir.mkdirs()
            }

            val image = File(folderDir, filename)
            fos = FileOutputStream(image)
            imagePath = image.absolutePath

            fos.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imagePath
    }
    suspend fun getThemeImages(context: Context, folderName: String): List<MediaStoreTheme> {
        val images = mutableListOf<MediaStoreTheme>()
        withContext(Dispatchers.IO) {
            try {
                val folderDir = File(context.filesDir, folderName)
                if (!folderDir.exists()) {
                    return@withContext images
                }

                val files = folderDir.listFiles()
                files?.forEach { file ->
                    val uri = Uri.fromFile(file)
                    val displayName = file.name
                    val dateModified = Date(file.lastModified())
                    val contentUri = uri
                    val path = file.absolutePath
                    val image = MediaStoreTheme(0, displayName, dateModified, contentUri, path)
                    images += image
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return images
    }
}