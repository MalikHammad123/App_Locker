package com.example.gallery_vault.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.app_locker.R
import com.example.app_locker.adapters.adapter.ImagesAdapter
import com.example.app_locker.directory.utils.ImageStorageHelper
import com.example.app_locker.directory.utils.SavedImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
class ImageFragment : Fragment(), ImagesAdapter.ImageSelectionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter
    private lateinit var nophoto: ImageView

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val GALLERY_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        nophoto = view.findViewById(R.id.nophoto_iv)
        getAllImages()

        // Set OnClickListener for FAB
        val fab: FloatingActionButton = view.findViewById(R.id.floating_ab)
        fab.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }
        return view
    }

    private fun loadAndProcessImage(uri: Uri, context: Context) {
        val timestamp = System.currentTimeMillis() // Use current timestamp as an example
        Glide.with(this).asBitmap().load(uri).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val copiedImageUri = ImageStorageHelper.copyImageToNoMediaFolder(context, uri)
                if (copiedImageUri != null) {
                    adapter.removeImage(SavedImage(imagePath = uri.toString(), timestamp = timestamp))
                    Toast.makeText(context, "Image copied to .nomedia folder", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to copy image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Implementation for when the resource is cleared
            }
        })
    }


    private fun getAllImages() {
        lifecycleScope.launch {
            val imageList = ImageStorageHelper.getImagesFromNoMediaFolder(requireContext())
            if (imageList.isNotEmpty()) {
                nophoto.visibility = View.GONE
            }
            adapter = ImagesAdapter(imageList.toMutableList(), this@ImageFragment)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImageUri: Uri? = data.data
                selectedImageUri?.let {
                    loadAndProcessImage(it, requireContext())
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onImageSelected(image: SavedImage) {
        image.imagePath?.let { imagePath ->
            val copiedImageUri = ImageStorageHelper.moveImageToGallery(requireContext(), imagePath, image.timestamp)
            if (copiedImageUri != null) {
                Toast.makeText(requireContext(), "Image copied to gallery", Toast.LENGTH_SHORT).show()
                adapter.removeImage(image)
            } else {
                Toast.makeText(requireContext(), "Failed to copy image", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

