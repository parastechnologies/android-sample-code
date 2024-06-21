package com.highenergymind.view.activity.onboardexplore

import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityOnBoardingExploreBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import com.highenergymind.view.activity.login.LoginActivity


class OnBoardingExploreActivity : BaseActivity<ActivityOnBoardingExploreBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_on_boarding_explore
    }

    override fun initView() {
        fullScreenStatusBar(isTextBlack = false)
        onClick()
    }

    private fun onClick() {
        binding.apply {

        }
        binding.btnContinue.setOnClickListener {
            intentComponent(SignUpProcessActivity::class.java, null)
        }

        binding.btnLogin.setOnClickListener {
            intentComponent(LoginActivity::class.java, null)
        }

    }

}