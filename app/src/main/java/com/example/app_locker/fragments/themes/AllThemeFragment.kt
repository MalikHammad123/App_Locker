package com.example.app_locker.fragments.themes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.adapters.adapter.ThemeAdapter

class AllThemeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ThemeAdapter
    private lateinit var images: MutableList<Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_theme, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        images = mutableListOf(
            R.drawable.a,
            R.drawable.b,
            R.drawable.theme1,
            R.drawable.c,
            R.drawable.abc,
            R.drawable.d,
            R.drawable.lock,
            R.drawable.e,
            R.drawable.c,
            R.drawable.abc,
            R.drawable.b,
            R.drawable.theme1,
            R.drawable.c,
            R.drawable.a,
            R.drawable.d,
            R.drawable.abc,
            R.drawable.d,
            R.drawable.lock,
            R.drawable.theme1
            // Add more image resources as needed
        )
        imageAdapter = ThemeAdapter(requireContext(), images)
        recyclerView.adapter = imageAdapter
    }

}


