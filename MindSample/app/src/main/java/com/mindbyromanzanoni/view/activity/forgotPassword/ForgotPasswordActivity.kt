package com.mindbyromanzanoni.view.activity.forgotPassword

import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityForgotPasswordBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.applyTextWatcherAndFilter
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.view.activity.verificationCode.VerificationCodeActivity
import com.mindbyromanzanoni.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    private val viewModal: AuthViewModel by viewModels()
    var activity = this@ForgotPasswordActivity

    @Inject
    lateinit var validator: Validator

    override fun getLayoutRes() = R.layout.activity_forgot_password

    override fun initView() {
        getWatcherEmail()
        onClickListener()
        setToolbar()
        observeDataFromViewModal()
    }

    private fun getWatcherEmail() {
        binding.etEmail.applyTextWatcherAndFilter(validator, binding.ivTick)
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.visibility = View.GONE
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    private fun onClickListener() {
        binding.apply {
            btnSubmit.setOnClickListener {
                if (validator.validateForgotPassword(activity,binding)){
                    RunInScope.ioThread {
                        viewModal.hitForgotPasswordApi()
                    }
                }
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.forgotPasswordSharedFlow.collectLatest {isResponse->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            val bundle = bundleOf(AppConstants.SCREEN_TYPE to AppConstants.FORGOT_PASSWORD, AppConstants.USER_EMAIL to viewModal.email.get().toString())
                            launchActivity<VerificationCodeActivity>(0, bundle) { }
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }
                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }

        viewModal.showLoading.observe(activity) {
            if (it) {
                MyProgressBar.showProgress(activity)
            } else {
                MyProgressBar.hideProgress()
            }
        }
    }
}