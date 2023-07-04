package com.example.app_locker.adapters.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_locker.R
import android.widget.Button
import com.example.app_locker.directory.utils.SavedImage

class ImagesAdapter(
    private val imageList: MutableList<SavedImage>,
    private val imageSelectionListener: ImageSelectionListener
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_iv)
        val revertButton: ImageView = itemView.findViewById(R.id.revert_Image_iv)

        fun bind(image: SavedImage) {
            Glide.with(imageView.context)
                .load(image.imagePath)
                .into(imageView)

            revertButton.setOnClickListener {
                imageSelectionListener.onImageSelected(image)
            }
        }
    }

    interface ImageSelectionListener {
        fun onImageSelected(image: SavedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.images_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount() = imageList.size

    fun removeImage(image: SavedImage) {
        val position = imageList.indexOf(image)
        if (position != -1) {
            imageList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}


