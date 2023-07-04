package com.example.app_locker.adapters.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.directory.utils.SavedVideo

class VideosAdapter(
    private val videoList: List<SavedVideo>,
    private val revertClickListener: (videoPath: String) -> Unit
) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView: VideoView = itemView.findViewById(R.id.video_vv)
        private val revertButton: ImageView = itemView.findViewById(R.id.revert_Video_Button)

        fun bind(video: SavedVideo) {
            try {
                videoView.setVideoURI(Uri.parse(video.videoPath))
                videoView.setOnPreparedListener { mediaPlayer ->
                    // Adjust the video view size to match the video's aspect ratio
                    val videoWidth = mediaPlayer.videoWidth
                    val videoHeight = mediaPlayer.videoHeight
                    val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()

                    val layoutParams = videoView.layoutParams
                    layoutParams.width = videoView.width
                    layoutParams.height = (videoView.width / videoProportion).toInt()
                    videoView.layoutParams = layoutParams

                    // Start playing the video
                    mediaPlayer.start()
                }

                revertButton.setOnClickListener {
                    revertClickListener.invoke(video.videoPath)
                }
            } catch (e: Exception) {
                // Debugging: log exceptions
                Log.e("VideosAdapter", "Failed to load video from path: ${video.videoPath}", e)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.videos_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount() = videoList.size
}



