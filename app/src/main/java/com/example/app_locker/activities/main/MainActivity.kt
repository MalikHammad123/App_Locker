package com.example.app_locker.activities.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager.widget.ViewPager
import com.example.app_locker.R
import com.example.app_locker.activities.setting.Setting
import com.example.app_locker.activities.theme.Theme
import com.example.app_locker.adapters.viewpager.MainViewPagerAdapter
import com.example.app_locker.databinding.ActivityMainBinding
import com.example.app_locker.fragments.vip.VipFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabLayout: TabLayout = binding.tabLayout
        val viewPager: ViewPager = binding.viewPager

        viewPager.adapter = MainViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        val materialToolbar: MaterialToolbar = binding.materialTb
        materialToolbar.setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }

        materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.premium -> {
                    val fragment = VipFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .commit()
                    true
                }
                R.id.theme -> {
                    startActivity(Intent(this, Theme::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.unlockapp_dialogue_box)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val unlockTv: TextView = dialog.findViewById(R.id.unlock_tv)
        unlockTv.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }

        val lockTv: TextView = dialog.findViewById(R.id.lock_tv)
        lockTv.setOnClickListener {
            val intent = Intent(this, Theme::class.java)
            startActivity(intent)
        }

        dialog.show()
    }
}