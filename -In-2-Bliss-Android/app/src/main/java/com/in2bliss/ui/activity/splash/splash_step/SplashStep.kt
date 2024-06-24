package com.in2bliss.ui.activity.splash.splash_step

import androidx.core.os.bundleOf
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivitySplashStepBinding
import com.in2bliss.ui.activity.auth.stepOne.StepOneActivity
import com.in2bliss.ui.activity.auth.stepTwo.StepTwoActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashStep : BaseActivity<ActivitySplashStepBinding>(
    layout = R.layout.activity_splash_step
) {

    override fun init() {
        onclick()
    }

    private fun onclick() {
        binding.btnContinue.setOnClickListener {
            intent(
                destination = StepOneActivity::class.java
            )
        }
        binding.tvContinueHere.setOnClickListener {
            intent(
                destination = StepOneActivity::class.java
            )
        }
        binding.btnLogin.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.IS_LOGIN to true
            )
            intent(
                destination = StepTwoActivity::class.java,
                bundle = bundle
            )
        }
    }
}