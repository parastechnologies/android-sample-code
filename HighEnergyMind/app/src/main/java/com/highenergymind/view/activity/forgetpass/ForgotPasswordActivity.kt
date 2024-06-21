package com.highenergymind.view.activity.forgetpass

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.ActivityForgotPasswordBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.forgotOtpVerification.ForgotOtpVerificationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    val viewModel by viewModels<ForgotPasswordViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_forgot_password
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        onClick()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                forgotPasswordResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        showToast(response.message)
                        showToast(response.data?.emailOtp)
                        response.data?.let { it1 -> viewModel.sharedPrefs.saveUserData(it1) }
                        intentComponent(ForgotOtpVerificationActivity::class.java)
                    })
                }
            }
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
        }
    }

    private fun onClick() {
        binding.apply {
            backBtn.backIV.setOnClickListener {
                finish()
            }
            submitBtn.setOnClickListener {
                if (validator.forgotValidation(binding)) {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.EMAIL] = emailEdt.text?.trim()?.toString()!!
                        map[ApiConstant.TYPE] = "1" /* 1 means send otp on forgotPassword*/
                        forgotPasswordApi()
                    }
                }
            }
        }
    }


}