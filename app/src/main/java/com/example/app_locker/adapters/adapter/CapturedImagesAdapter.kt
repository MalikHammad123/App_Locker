package com.example.app_locker.adapters.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.squareup.picasso.Picasso
import java.io.File

class CapturedImagesAdapter : RecyclerView.Adapter<CapturedImagesAdapter.PictureViewHolder>() {
    private var picturesList: List<File> = emptyList()
    private val selectedPositions: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.intruder_image_recyclerview, parent, false)
        return PictureViewHolder(view).apply {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(position)
                    return@setOnLongClickListener true
                }
                false
            }
        }
    }

    private fun toggleSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val pictureFile = picturesList[position]
        Picasso.get().load(pictureFile).into(holder.imageView)
        val isSelected = selectedPositions.contains(position)
        holder.itemView.isActivated = isSelected
        // Add any visual indicator for selection, such as changing the background color of the selected item
        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.teal_200 else R.color.canvas
            )
        )
    }

    override fun getItemCount(): Int {
        return picturesList.size
    }

    fun setPictures(pictures: List<File>) {
        picturesList = pictures
        notifyDataSetChanged()
    }

    fun getSelectedPositions(): List<Int> {
        return selectedPositions
    }

    fun getPictureFile(position: Int): File? {
        if (position in 0 until picturesList.size) {
            return picturesList[position]
        }
        return null
    }

    fun clearSelections() {
        selectedPositions.clear()
    }

    fun removePicture(pictureFile: File) {
        val position = picturesList.indexOf(pictureFile)
        if (position != -1) {
            picturesList = picturesList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }

    inner class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.intruder_iv)
    }
}