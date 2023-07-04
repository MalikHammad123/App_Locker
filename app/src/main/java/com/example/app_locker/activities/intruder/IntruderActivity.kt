package com.example.app_locker.activities.intruder

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.adapters.adapter.CapturedImagesAdapter
import com.example.app_locker.bottomsheets.SecurityQuestionsBottomSheet
import com.example.app_locker.bottomsheets.WrongAttemptBottomSheet
import com.example.app_locker.databinding.ActivityIntruderBinding
import com.google.android.material.appbar.MaterialToolbar
import java.io.File


class IntruderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntruderBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CapturedImagesAdapter
    lateinit var nophoto: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switchView: Switch

    companion object {
        private const val PREFS_NAME = "IntruderPrefs"
        private const val SWITCH_STATE_KEY = "switchState"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntruderBinding.inflate(layoutInflater)
        setContentView(binding.root)




        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        switchView = findViewById(R.id.intruder_sv)

        // Retrieve the switch state from SharedPreferences
        val switchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)

        // Set the initial state of the switch
        switchView.isChecked = switchState

        // Listen for switch state changes and save it in SharedPreferences
        switchView.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isChecked).apply()
        }



        nophoto = findViewById(R.id.nophoto_iv)
        recyclerView = findViewById(R.id.intruder_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = CapturedImagesAdapter()
        recyclerView.adapter = adapter

        // Load and display the pictures in the adapter
        val picturesList = loadPicturesFromCache()
        if (picturesList.size > 0) {
            nophoto.visibility = View.GONE
        }
        adapter.setPictures(picturesList)

        val materialToolbar: MaterialToolbar = findViewById(R.id.material_tb)
        materialToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                    if (picturesList.size > 0) {
                        showDialog()

                    } else {
                        Toast.makeText(this, "No Pictures to Delete", Toast.LENGTH_SHORT).show()
                    }


                    true
                }
                R.id.setting -> {
                    openBottomSheet()
                    true
                }
                else -> false
            }
        }
        materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun showDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.intruder_pic_delete_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = dialog.findViewById<TextView>(R.id.cancel_tv)
        cancel.setOnClickListener {
            // Add code to handle click events for the "Cross" button here
            dialog.dismiss() // close the dialog
        }

        val ok = dialog.findViewById<TextView>(R.id.delete_tv)
        ok.setOnClickListener {
            deletepictures()
            dialog.dismiss() // close the dialog

        }

        dialog.show()

    }

    private fun openBottomSheet() {

        val bottomSheetFragment = WrongAttemptBottomSheet()
        bottomSheetFragment.show(supportFragmentManager, "bottomSheetFragment")
    }

    private fun deletepictures() {
        val selectedPositions = adapter.getSelectedPositions()
        val selectedPictures =
            selectedPositions.mapNotNull { position -> adapter.getPictureFile(position) }

        // Delete selected pictures from cache and data list
        for (pictureFile in selectedPictures) {
            if (pictureFile.delete()) {
                adapter.removePicture(pictureFile)
            }
        }

        adapter.clearSelections()
        adapter.notifyDataSetChanged()
    }

    private fun loadPicturesFromCache(): List<File> {
        val cacheDir = cacheDir
        val picturesList: MutableList<File> = mutableListOf()

        val files = cacheDir.listFiles()
        files?.let {
            for (file in it) {
                if (file.isFile) {
                    picturesList.add(file)
                }
            }
        }
        return picturesList
    }
}

