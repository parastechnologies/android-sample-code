package com.highenergymind.view.fragment.onboard

import android.text.method.ScrollingMovementMethod
import com.highenergymind.R
import com.highenergymind.base.BaseFragment
import com.highenergymind.databinding.FragmentOnBoardThBinding

class OnBoardThreeFragment : BaseFragment<FragmentOnBoardThBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_on_board_th
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