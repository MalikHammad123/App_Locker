package com.example.app_locker.activities.theme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.app_locker.R
import com.example.app_locker.adapters.viewpager.ThemePagerAdapter
import com.example.app_locker.adapters.viewpager.VaultPagerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class Theme : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = ThemePagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        val materialToolbar: MaterialToolbar = findViewById(R.id.material_tb)
        materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}