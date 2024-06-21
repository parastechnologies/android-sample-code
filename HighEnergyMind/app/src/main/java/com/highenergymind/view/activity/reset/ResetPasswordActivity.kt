package com.highenergymind.view.activity.reset

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.databinding.ActivityResetPasswordBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity<ActivityResetPasswordBinding>() {
    val viewModel by viewModels<ResetPasswordViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_reset_password
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        onClick()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                resetPasswordResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        showToast(response.message)
                        intentComponent(LoginActivity::class.java)
                        finishAffinity()
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
            ivEye1.setOnClickListener {
                if (etNewPas.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    etNewPas.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    ivEye1.setImageResource(R.drawable.ic_eye_show)
                    etNewPas.setSelection(etNewPas.text?.length ?: 0)
                } else {
                    etNewPas.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye1.setImageResource(R.drawable.ic_eye_hide)
                    etNewPas.setSelection(etNewPas.text?.length ?: 0)
                }
            }

            ivEye2.setOnClickListener {
                if (etConfirmPas.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    etConfirmPas.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    ivEye2.setImageResource(R.drawable.ic_eye_show)
                    etConfirmPas.setSelection(etConfirmPas.text?.length ?: 0)
                } else {
                    etConfirmPas.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye2.setImageResource(R.drawable.ic_eye_hide)
                    etConfirmPas.setSelection(etConfirmPas.text?.length ?: 0)
                }
            }
            btnReset.setOnClickListener {
                if (validator.resetPasValidator(binding)) {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.PWD] = etNewPas.text?.trim()?.toString()!!
                        map[ApiConstant.EMAIL] = sharedPrefs.getUserData()?.email!!
                        resetPasswordApi()
                    }
                }
            }
        }
        binding.backBtn.backIV.setOnClickListener {
            finish()
        }
    }


}