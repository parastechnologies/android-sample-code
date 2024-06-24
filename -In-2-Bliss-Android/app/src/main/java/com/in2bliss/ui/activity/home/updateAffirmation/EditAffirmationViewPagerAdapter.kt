package com.in2bliss.ui.activity.home.updateAffirmation

import android.os.DeadObjectException
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.in2bliss.ui.activity.home.fragment.notification.read.ReadFragment
import com.in2bliss.ui.activity.home.fragment.notification.unread.UnReadFragment
import com.in2bliss.ui.activity.home.fragment.updateAffirmation.audio.AudioFragment
import com.in2bliss.ui.activity.home.fragment.updateAffirmation.category.CategoryFragment
import com.in2bliss.ui.activity.home.fragment.updateAffirmation.detail.DetailsFragment

class EditAffirmationViewPagerAdapter (fm: FragmentManager, private var tabCount: Int) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return tabCount
        }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DetailsFragment()
            1 -> AudioFragment()
            2 -> CategoryFragment()
            else -> DetailsFragment()
        }
    }
}

