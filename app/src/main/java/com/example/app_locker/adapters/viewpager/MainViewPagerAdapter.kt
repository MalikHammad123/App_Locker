package com.example.app_locker.adapters.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.app_locker.fragments.main.AdvancedFragment
import com.example.app_locker.fragments.main.AllAppFragment


class MainViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentList = listOf(
        AllAppFragment(),
        AdvancedFragment()
    )

    private val pageTitleList = listOf(
        "All Apps",
        "Advanced"
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