package com.app.muselink.ui.adapter.profile

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.ui.fragments.profile.AudioSoundProgressFragment
import com.app.muselink.ui.fragments.profile.CommentFragmentProfile
import com.app.muselink.ui.fragments.profile.VisualizerFragment
import com.app.muselink.util.WrappingViewPager


@SuppressLint("WrongConstant")
class AdapterVsualizers internal constructor(fm: FragmentManager,val modalAudioFile: ModalAudioFile) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var mCurrentFragment: Fragment? = null
    var registeredFragments = SparseArray<Fragment>()
    private var mCurrentPosition = -1
    private val COUNT = 3
    private var fragmentList: MutableList<Fragment> = ArrayList()

    fun setModalAudio(){

    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment =  AudioSoundProgressFragment(modalAudioFile)
                fragmentList.add(fragment)
            }
            1 -> {
                fragment =  VisualizerFragment(modalAudioFile)
                fragmentList.add(fragment)
            }
            2 -> {
                fragment =  CommentFragmentProfile(modalAudioFile)
                fragmentList.add(fragment)
            }
        }
        return fragment!!

    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        if (position != mCurrentPosition && container is WrappingViewPager) {
            val fragment = `object` as Fragment
            if (fragment != null && fragment.view != null) {
                mCurrentPosition = position
                container.measureCurrentView(fragment.view)
            }
        }

    }


    fun getVisualizerFragment(): VisualizerFragment? {
        var fragmentSoundFile: VisualizerFragment? = null
        for (i in 0 until fragmentList.size) {
            val fragment = fragmentList.get(i)
            if (fragment is VisualizerFragment) {
                fragmentSoundFile = fragment
                break
            }
        }
        return fragmentSoundFile
    }

    fun getAudioVisualizerFragment(): AudioSoundProgressFragment? {
        var fragmentSoundFilePro: AudioSoundProgressFragment? = null
        for (i in 0 until fragmentList.size) {
            val fragment = fragmentList.get(i)
            if (fragment is AudioSoundProgressFragment) {
                fragmentSoundFilePro = fragment
                break
            }
        }
        return fragmentSoundFilePro
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }


    override fun getCount(): Int {
        return COUNT
    }


}