package com.example.app_locker.fragments.setTheme

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.app_locker.R
import com.example.app_locker.activities.login.LoginActivity
import com.example.app_locker.directory.utils.MediaStoreTheme

class ImageDetailFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var setImageButton: TextView
    private lateinit var selectedImage: MediaStoreTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_detail, container, false)
        imageView = view.findViewById(R.id.image_iv)
        setImageButton = view.findViewById(R.id.set_image_theme_button)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
        setupSetImageButton()
    }

    private fun loadImage() {
        Glide.with(requireContext())
            .load(selectedImage.path)
            .into(imageView)
    }

    private fun setupSetImageButton() {
        setImageButton.setOnClickListener {
            val backgroundImage = BitmapDrawable(resources, getBitmapFromImageView(imageView))
            LoginActivity.setBackgroundImage(backgroundImage)
            // Close the fragment or perform any other action
        }
    }

    private fun getBitmapFromImageView(imageView: ImageView): Bitmap {
        val drawable = imageView.drawable
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        return bitmap
    }

    companion object {
        fun newInstance(image: MediaStoreTheme): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            fragment.selectedImage = image
            return fragment
        }
    }
}
