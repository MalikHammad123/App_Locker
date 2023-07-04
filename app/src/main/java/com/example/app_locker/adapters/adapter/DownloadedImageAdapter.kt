package com.example.app_locker.adapters.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.app_locker.R
import com.example.app_locker.directory.utils.MediaStoreTheme

class DownloadedImageAdapter(private val imageList: List<MediaStoreTheme>,private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<DownloadedImageAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun onItemClick(image: MediaStoreTheme)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.download_iv)

        fun bind(image: MediaStoreTheme) {
            try {
                //val fis = FileInputStream(image.imagePath)
                itemView.setOnClickListener {
                    itemClickListener?.onItemClick(image)
                }
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(image.path)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            imageView.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
                // Debugging: log successful image load
                Log.d("DownloadedImageAdapter", "Loaded image from path: ${image.path}")
            } catch (e: Exception) {
                // Debugging: log exceptions
                Log.e("DownloadedImageAdapter", "Failed to load image from path: ${image.path}", e)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.downloaded_theme_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount() = imageList.size
}

