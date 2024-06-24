package com.in2bliss.ui.activity.home.notification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.in2bliss.ui.activity.home.fragment.notification.read.ReadFragment
import com.in2bliss.ui.activity.home.fragment.notification.unread.UnReadFragment

class NotificationViewPagerAdapter (fm: FragmentManager, private var tabCount: Int) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return tabCount
        }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UnReadFragment()
            1 -> ReadFragment()
            else -> UnReadFragment()
        }
    }
}
