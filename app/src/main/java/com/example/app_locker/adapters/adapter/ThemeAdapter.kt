package com.example.app_locker.adapters.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_locker.R
import com.example.app_locker.fragments.download.DownloadActivity.Companion.startDownloadActivity

class ThemeAdapter(
    private val context: Context,
    private val images: MutableList<Int>
) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.theme_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.theme_iv)

        fun bind(image: Int) {
            Glide.with(context)
                .load(image)
                .into(imageView)

            itemView.setOnClickListener {
                val imageUrl = image // get the URL from the image object, replace with your logic
                startDownloadActivity(context, imageUrl)
            }
        }
    }
}