import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
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
import com.example.app_locker.adapters.adapter.VideosAdapter
import com.example.app_locker.directory.utils.VideoStorageHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File

class VideoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideosAdapter
    private lateinit var noVideo: ImageView

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val GALLERY_REQUEST_CODE = 1
        private const val REQUEST_MANAGE_EXTERNAL_STORAGE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.video_recycler_view)
        noVideo = view.findViewById(R.id.novideo_iv)
        getAllVideos()

        // Set OnClickListener for FAB
        val fab: FloatingActionButton = view.findViewById(R.id.video_floating_ab)
        fab.setOnClickListener {
            if (checkPermission()) {
                requestManageExternalStoragePermission()
            } else {
                requestPermission()
            }
        }
        return view
    }

    private fun loadAndProcessVideo(uri: Uri, context: Context) {
        Glide.with(this).asBitmap().load(uri).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                VideoStorageHelper.copyVideoToNoMediaFolder(context, uri)
                getAllVideos()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Implementation for when the resource is cleared
            }
        })
    }

    private fun getAllVideos() {
        lifecycleScope.launch {
            val videoList = VideoStorageHelper.getVideosFromNoMediaFolder(requireContext())
            if (videoList.isNotEmpty()) {
                noVideo.visibility = View.GONE
            }
            adapter = VideosAdapter(videoList) { videoPath ->
                revertVideoToGallery(videoPath)
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "video/*"
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestManageExternalStoragePermission()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                val videoUri: Uri? = data.data
                videoUri?.let {
                    loadAndProcessVideo(it, requireContext())
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

    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                openGallery()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE)
            }
        } else {
            openGallery()
        }
    }

    private fun revertVideoToGallery(videoPath: String) {
        val file = File(videoPath)
        if (file.exists()) {
            val timestamp = file.nameWithoutExtension.toLongOrNull()
            if (timestamp != null) {
                val uri = VideoStorageHelper.moveVideoToGallery(requireContext(), videoPath, timestamp)
                if (uri != null) {
                    // Video moved successfully, update the UI
                    Toast.makeText(requireContext(), "Video moved to gallery", Toast.LENGTH_SHORT).show()
                    getAllVideos()
                } else {
                    Toast.makeText(requireContext(), "Failed to move video to gallery", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

