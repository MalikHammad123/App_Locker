package com.example.app_locker.activities.vault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.app_locker.R
import com.example.app_locker.adapters.viewpager.VaultPagerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class Vault : AppCompatActivity() {


    private val REQUEST_CODE_GALLERY = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = VaultPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        val materialToolbar: MaterialToolbar = findViewById(R.id.material_tb)
        materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // Handle the selected image URI here
        }
    }
}