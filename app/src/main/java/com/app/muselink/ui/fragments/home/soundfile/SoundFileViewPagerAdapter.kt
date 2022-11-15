package com.app.muselink.ui.fragments.home.soundfile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class SoundFileViewPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments: ArrayList<Fragment> = ArrayList()
    fun add(fragment: Fragment?) {
        fragments.add(fragment!!)
    }
    fun getFragmentAccordingPos(position: Int) :Fragment{
        return fragments[position]
    }
    override fun getCount(): Int {
        return fragments.size
    }
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}