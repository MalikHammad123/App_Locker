package com.example.app_locker.directory.utils

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.app_locker.directory.utils.ImageStorageHelper.createFolderWithNoMedia
import java.io.File

object VideoStorageHelper {


    fun copyVideoToNoMediaFolder(context: Context, sourceUri: Uri): Uri? {
        Log.d(TAG,"VIDEO URI before coping "+sourceUri)
        try {
            val resolver = context.contentResolver

            val destFolderPath = createFolderWithNoMedia(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath ?: "")
            val destFileName = sourceUri.lastPathSegment
            val destFile = File(destFolderPath, destFileName)

            resolver.openOutputStream(destFile.toUri())?.use { outputStream ->
                resolver.openInputStream(sourceUri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            deleteVideoFile(context, sourceUri) // Call deleteVideoFile after copying the video

            return destFile.toUri()
        } catch (e: Exception) {
            Log.e(TAG, "Error copying video to .nomedia folder: ${e.message}")
            return null
        }
    }
    private fun deleteVideoFile(context: Context, videoUri: Uri): Boolean {
    Log.d(TAG, "VIDEO URI before deleting: $videoUri")
    return try {
        val contentResolver: ContentResolver = context.contentResolver

        // Check if the video exists in the media store
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection = MediaStore.Video.Media.DATA + " = ?"
        val selectionArgs = arrayOf(getRealPathFromUri(context, videoUri))
        val cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null)
        val videoId = if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            cursor.getLong(columnIndex)
        } else {
            null
        }
        cursor?.close()

        if (videoId != null) {
            val videoContentUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId.toString())

            // Delete the video using the obtained content URI
            contentResolver.delete(videoContentUri, null, null)
            true
        } else {
            Log.d(TAG, "Video not found in the media store.")
            false
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting video file: ${e.message}")
        false
    }
}
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
        }
        cursor?.close()
        return realPath
    }
    fun getVideosFromNoMediaFolder(context: Context): List<SavedVideo> {
        val savedVideos = mutableListOf<SavedVideo>()
        val folderPath = createFolderWithNoMedia(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath ?: "")
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                val savedVideo = SavedVideo(videoPath = file.absolutePath, timestamp = System.currentTimeMillis())
                savedVideos.add(savedVideo)
            }
        }
        else {
            folder.mkdirs()
        }
        return savedVideos
    }

    fun moveVideoToGallery(context: Context, videoPath: String, timestamp: Long): Uri? {
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

            val destFileName = "$timestamp.mp4"
            val destFile = File(destFolder, destFileName)

            val sourceFile = File(videoPath)

            // Move the file by renaming it
            if (sourceFile.renameTo(destFile)) {
                // Notify the system about the moved video file
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(destFile)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)

                // Delete the original file from the .nomedia directory
                sourceFile.delete()

                return contentUri
            } else {
                Log.e(TAG, "Failed to move video file")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error moving video to gallery: ${e.message}")
            return null
        }
    }


}



