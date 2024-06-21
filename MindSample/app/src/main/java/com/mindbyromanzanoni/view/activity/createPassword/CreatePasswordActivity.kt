package com.mindbyromanzanoni.view.activity.createPassword

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityCreatePasswordBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.view.bottomsheet.passwordchange.BottomSheetPasswordChange
import com.mindbyromanzanoni.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreatePasswordActivity : BaseActivity<ActivityCreatePasswordBinding>() {
    private val viewModal: AuthViewModel by viewModels()
    var activity = this@CreatePasswordActivity

    @Inject
    lateinit var validator: Validator

    override fun getLayoutRes() = R.layout.activity_create_password

    override fun initView() {
        getIntentData()
        onClickListener()
        setToolbar()
        observeDataFromViewModal()
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun onClickListener() {
        binding.apply {
            btnCreatePassword.setOnClickListener {
                if (validator.validateResetPassword(activity,binding)){
                    RunInScope.ioThread {
                        viewModal.hitResetPasswordApi()
                    }
                }
            }
        }
    }

    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null){
            viewModal.email.set(intent.getString(AppConstants.USER_EMAIL).toString())
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.resetPasswordSharedFlow.collectLatest {isResponse->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            val bottomSheetPasswordChange = BottomSheetPasswordChange()
                            bottomSheetPasswordChange.show(supportFragmentManager, "")
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