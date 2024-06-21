package com.highenergymind.view.fragment.onboard

import android.text.method.ScrollingMovementMethod
import com.highenergymind.R
import com.highenergymind.base.BaseFragment
import com.highenergymind.databinding.FragmentOnboardScreenOBinding


class OnBoardOneFragment : BaseFragment<FragmentOnboardScreenOBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_onboard_screen_o
    }

    override fun initViewWithData() {
        clicks()
    }

    private fun clicks() {
        mBinding.apply {
            contentTV.movementMethod = ScrollingMovementMethod()
            flayMoon.playAnimation()
        }
    }


}