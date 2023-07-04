package com.example.app_locker.fragments.download

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.app_locker.R
import com.example.app_locker.directory.utils.ThemeStorageHelper
import com.google.android.material.appbar.MaterialToolbar
class DownloadActivity : AppCompatActivity() {

    private lateinit var imageViewFullscreen: ImageView
    private lateinit var buttonDownload: Button
    private var imageUrl: Int = 0
    /////////////////////////////////////initialization for showing in downloaded fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        val materialToolbar: MaterialToolbar = findViewById(R.id.material_tb)
        materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        imageViewFullscreen = findViewById(R.id.imageViewFullscreen)
        buttonDownload = findViewById(R.id.buttonDownload)
        // Get the image URL passed from the RecyclerView adapter
        imageUrl = intent?.extras?.getInt("image_url") ?: 0

        // Load the image into the full-screen ImageView using Glide
        Glide.with(this).load(imageUrl).into(imageViewFullscreen)

        buttonDownload.setOnClickListener {
            // Check for permission to write to external storage
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_WRITE_EXTERNAL_STORAGE
                )
            } else {
                // Permission is already granted, start the download
                downloadImage()
                Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }


    }

    private fun downloadImage() {
        // Use Glide to load the image and save it to external storage
        Glide.with(this).asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Create a file to save the image
                imageViewFullscreen.setImageBitmap(resource)
                ThemeStorageHelper.saveThemeToStorage(
                    this@DownloadActivity, resource, "hammad_themes"
                )
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Not needed for this implementation
            }
        })
    }


    companion object {
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE = 100

        fun startDownloadActivity(context: Context, imageUrl: Int) {
            val intent = Intent(context, DownloadActivity::class.java).apply {
                putExtra("image_url", imageUrl)
            }
            context.startActivity(intent)
        }
    }


}