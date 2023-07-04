package com.example.app_locker.adapters.viewpager

import VideoFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.app_locker.fragments.themes.AllThemeFragment
import com.example.app_locker.fragments.themes.DownloadedThemeFragment


class ThemePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentList = listOf(
        AllThemeFragment(),
        DownloadedThemeFragment()
    )

    private val pageTitleList = listOf(
        "All Themes",
        "Downloaded"
    )

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitleList[position]
    }
}