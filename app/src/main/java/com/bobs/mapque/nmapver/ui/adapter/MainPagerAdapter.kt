package com.bobs.mapque.nmapver.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bobs.baselibrary.util.loge

class MainPagerAdapter(fragmentManager: FragmentManager, private val fragmentList: List<Fragment>) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int = fragmentList.size
//    override fun getCount(): Int = 2
}