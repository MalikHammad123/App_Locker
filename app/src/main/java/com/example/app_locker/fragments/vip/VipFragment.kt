package com.example.app_locker.fragments.vip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import com.example.app_locker.R

class VipFragment : Fragment() {

    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vip, container, false)

        imageView = view.findViewById(R.id.close_iv)
        imageView.setOnClickListener {
            closeFragment()
        }

        return view
    }

    private fun closeFragment() {
        // Get the FragmentManager
        val fragmentManager = requireActivity().supportFragmentManager

        // Remove the VipFragment from the container
        fragmentManager.beginTransaction().remove(this).commit()
    }
}


