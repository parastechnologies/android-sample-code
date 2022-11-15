package com.app.muselink.ui.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.ui.fragments.home.soundfile.SoundFileViewPagerFragment
import com.app.muselink.ui.fragments.home.viewpager.SoundFileFragment
import com.app.muselink.ui.fragments.home.viewpager.SoundFileProFragment
import com.app.muselink.ui.fragments.home.viewpager.comments.CommentsFragment

class DashboardSliderAdapter internal constructor(
    fm: FragmentManager,
    val dashBoardFragment: DashBoardFragment
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mCurrentFragment: Fragment? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()
    private val COUNT = 3
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = SoundFileProFragment(dashBoardFragment)
                fragmentList.add(fragment)
            }
            1 -> {
                fragment = SoundFileViewPagerFragment(dashBoardFragment)
                fragmentList.add(fragment)
            }
            2 -> {
                fragment = CommentsFragment(dashBoardFragment)
                fragmentList.add(fragment)
            }
        }
        return fragment!!
    }

    fun getCommentsFragment(): CommentsFragment?{
        var fragmentSoundFile: CommentsFragment? = null
        for (i in 0 until fragmentList.size) {
            val fragment = fragmentList[i]
            if (fragment is CommentsFragment) {
                fragmentSoundFile = fragment
                break
            }
        }
        return fragmentSoundFile
    }

    fun getSoundFileViewPagerFragment(): SoundFileViewPagerFragment? {
        var fragmentSoundFile: SoundFileViewPagerFragment? = null
        for (i in 0 until fragmentList.size) {
            val fragment = fragmentList[i]
            if (fragment is SoundFileViewPagerFragment) {
                fragmentSoundFile = fragment
                break
            }
        }
        return fragmentSoundFile
    }

    fun getSoundFileFragment(): SoundFileFragment? {
        var fragmentSoundFile: SoundFileFragment? = null
        for (i in 0 until fragmentList.size) {
            val fragment = fragmentList[i]
            if (fragment is SoundFileFragment) {
                fragmentSoundFile = fragment
                break
            }
        }
        return fragmentSoundFile
    }

    fun getSoundFileProFragment(): SoundFileProFragment? {
        var fragmentSoundFilePro: SoundFileProFragment? = null
        for (i in 0 until fragmentList.size) {
            val fragment = fragmentList[i]
            if (fragment is SoundFileProFragment) {
                fragmentSoundFilePro = fragment
                break
            }
        }
        return fragmentSoundFilePro
    }

    fun getCurrentFragment(): Fragment? {
        return mCurrentFragment
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (getCurrentFragment() !== `object`) {
            mCurrentFragment = `object` as Fragment
        }
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getCount(): Int {
        return COUNT
    }
}