package com.example.app_locker.adapters.viewpager



import VideoFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.gallery_vault.fragments.ImageFragment


class VaultPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentList = listOf(
        ImageFragment(),
        VideoFragment()
    )

    private val pageTitleList = listOf(
        "Photos",
        "Videos"
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