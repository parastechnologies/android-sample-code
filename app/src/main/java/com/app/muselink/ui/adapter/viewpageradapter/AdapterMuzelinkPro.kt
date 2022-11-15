package com.app.muselink.ui.adapter.viewpageradapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.muselink.ui.fragments.muzelinkpro.*
import com.app.muselink.util.WrappingViewPager

@SuppressLint("WrongConstant")
class AdapterMuzelinkPro internal constructor(fm: FragmentManager) :  FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {

    private var mCurrentFragment: Fragment? = null
    var registeredFragments = SparseArray<Fragment>()
    private var mCurrentPosition = -1
    private val COUNT = 7

    override fun getCount(): Int {
        return COUNT
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

    fun getCurrentFragment(): Fragment? {
        return registeredFragments.get(mCurrentPosition)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 ->  ProSlide1()
            1 ->  ProSlide2()
            2 ->  ProSlide3()
            3 ->  ProSlide4()
            4 ->  Proslide5()
            5 ->  Proslide6()
            6 ->  ProSlide7()
            else -> ProSlide1()
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    //for set the height of the viewpager according to the vie
}