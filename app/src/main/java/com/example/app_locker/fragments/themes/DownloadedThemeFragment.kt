package com.example.app_locker.fragments.themes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.adapters.adapter.DownloadedImageAdapter
import com.example.app_locker.directory.utils.ImageStorageHelper
import com.example.app_locker.directory.utils.MediaStoreTheme
import com.example.app_locker.directory.utils.ThemeStorageHelper
import com.example.app_locker.fragments.setTheme.ImageDetailFragment
import kotlinx.coroutines.launch

class DownloadedThemeFragment : Fragment() , DownloadedImageAdapter.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadedImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDownloadedTheme()

        val view = inflater.inflate(R.layout.fragment_downloaded_theme, container, false)
        recyclerView = view.findViewById(R.id.downloaded_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDownloadedTheme()

    }
    fun getDownloadedTheme(){
        lifecycleScope.launch {
            val imagePaths = ThemeStorageHelper.getThemeImages(requireContext(), "hammad_themes")
            adapter = DownloadedImageAdapter(imagePaths, this@DownloadedThemeFragment)
            recyclerView.adapter = adapter
        }
    }

    override fun onItemClick(image: MediaStoreTheme) {
        val fragment = ImageDetailFragment.newInstance(image)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}

