package com.highenergymind.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.highenergymind.view.fragment.onboard.OnBoardOneFragment
import com.highenergymind.view.fragment.onboard.OnBoardTwoFragment
import com.highenergymind.view.fragment.onboard.OnBoardThreeFragment
import com.highenergymind.view.fragment.onboard.OnBoardingFourthFragment

@Suppress("DEPRECATION")
class OnboardAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return OnBoardOneFragment()
            }

            1 -> {
                return OnBoardTwoFragment()
            }

            2 -> {
                return OnBoardThreeFragment()
            }

            3 -> {

                return OnBoardingFourthFragment()
            }

            else -> {
                return OnBoardOneFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return ""
            }

            1 -> {
                return ""
            }

            2 -> {
                return ""
            }

            3 -> {

                return ""
            }
        }
        return super.getPageTitle(position)
    }

}
