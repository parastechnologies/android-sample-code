package com.mindbyromanzanoni.view.activity.changePassword

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityChangePasswordBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.showSuccessBarAlert
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@ChangePasswordActivity

    @Inject
    lateinit var validator: Validator

    override fun getLayoutRes(): Int = R.layout.activity_change_password

    override fun initView() {
        setToolbar()
        setOnClickListener()
        observeDataFromViewModal()
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun setOnClickListener() {
        binding.apply {
            btnCreatePassword.setOnClickListener {
                if (validator.validateChangePassword(activity,binding)){
                    RunInScope.ioThread {
                        viewModal.hitChangePasswordApi()
                    }
                }
            }
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.change_password)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.changePasswordSharedFlow.collectLatest {isResponse->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            showSuccessBarAlert(activity,getString(R.string.success),data.message ?: "Password Successfully Changed")
                            delay(1500)
                            finishActivity()
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