package com.highenergymind.view.fragment.onboard

import android.text.method.ScrollingMovementMethod
import com.highenergymind.R
import com.highenergymind.base.BaseFragment
import com.highenergymind.databinding.FragmentOnBoardTBinding

class OnBoardTwoFragment : BaseFragment<FragmentOnBoardTBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_on_board_t
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