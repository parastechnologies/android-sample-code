package com.app.muselink.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class WelcomeAdapter(
    manager: FragmentManager,private var  list: ArrayList<Fragment>
) : FragmentPagerAdapter(manager) {

//    override fun getItemCount(): Int {
//        return list.size
//    }
//    override fun createFragment(position: Int): Fragment {
//        return list[position]
//    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount(): Int {
        return 3
    }
}