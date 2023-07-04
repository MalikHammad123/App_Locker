package com.example.app_locker.directory.utils

import android.content.*
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import java.io.*
object ImageStorageHelper {
//////////////////////////////////for images //////////////////////////////////////////////////////


     fun copyImageToNoMediaFolder(context: Context, sourceUri: Uri): Uri? {
        try {
            val resolver = context.contentResolver

            val destFolderPath = createFolderWithNoMedia(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: "")
            val destFileName = sourceUri.lastPathSegment
            val destFile = File(destFolderPath, destFileName)

            resolver.openOutputStream(destFile.toUri())?.use { outputStream ->
                resolver.openInputStream(sourceUri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            deleteImageFile(context, sourceUri)
            return destFile.toUri()
        } catch (e: Exception) {
            Log.e(TAG, "Error copying image to .nomedia folder: ${e.message}")
            return null
        }
    }
     private fun deleteImageFile(context: Context, imageUri: Uri): Boolean {
        return try {
            context.contentResolver.delete(imageUri, null, null)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image file: ${e.message}")
            false
        }
    }
     fun createFolderWithNoMedia(directoryPath: String): String {
        val folder = File(directoryPath)
        if (folder.exists()) {
            return folder.absolutePath
        }
        if (folder.mkdirs()) {
            val noMediaFile = File(folder, ".nomedia")
            if (noMediaFile.createNewFile()) {
                return folder.absolutePath
            } else {
                noMediaFile.delete()
                folder.delete()
                throw IOException("Failed to create .nomedia file")
            }
        } else {
            throw IOException("Failed to create folder")
        }
    }
     fun getImagesFromNoMediaFolder(context: Context): List<SavedImage> {
        val savedImages = mutableListOf<SavedImage>()
        val folderPath = createFolderWithNoMedia(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: "")
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                val savedImage = SavedImage(imagePath = file.absolutePath, timestamp = System.currentTimeMillis())
                savedImages.add(savedImage)
            }
        }
        return savedImages
    }


    fun moveImageToGallery(context: Context, imagePath: String, timestamp: Long): Uri? {
        try {
            val resolver = context.contentResolver

            val destFolderName = "App_Locker"
            val destFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), destFolderName)

            // Create the destination folder if it doesn't exist
            if (!destFolder.exists()) {
                if (!destFolder.mkdirs()) {
                    Log.e(TAG, "Failed to create destination folder: $destFolderName")
                    return null
                }
            }

            val destFileName = "$timestamp.jpg"
            val destFile = File(destFolder, destFileName)

            val sourceFile = File(imagePath)

            // Move the file by renaming it
            if (sourceFile.renameTo(destFile)) {
                // Notify the system about the moved image file
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(destFile)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)

                // Delete the original file from the .nomedia directory
                sourceFile.delete()

                return contentUri
            } else {
                Log.e(TAG, "Failed to move image file")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error moving image to gallery: ${e.message}")
            return null
        }
    }


}
////////////////////////////////////////////////////////////////////////
