package com.highenergymind.view.activity.changepassword

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.databinding.ActivityChangePasswordBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {

    val viewModel by viewModels<ChangePasswordViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_change_password
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        onClick()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                changePasswordResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        showToast(response.message)
                        finish()
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
        binding.backBtn.backIV.setOnClickListener {
            finish()
        }
        binding.apply {
            ivEye1.setOnClickListener {
                if (etCurrentPas.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    etCurrentPas.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    ivEye1.setImageResource(R.drawable.ic_eye_show)
                    etCurrentPas.setSelection(etCurrentPas.text?.length ?: 0)
                } else {
                    etCurrentPas.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye1.setImageResource(R.drawable.ic_eye_hide)
                    etCurrentPas.setSelection(etCurrentPas.text?.length ?: 0)
                }
            }

            ivEye2.setOnClickListener {
                if (etNewPas.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    etNewPas.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    ivEye2.setImageResource(R.drawable.ic_eye_show)
                    etNewPas.setSelection(etNewPas.text?.length ?: 0)
                } else {
                    etNewPas.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye2.setImageResource(R.drawable.ic_eye_hide)
                    etNewPas.setSelection(etNewPas.text?.length ?: 0)
                }
            }
            ivEye3.setOnClickListener {
                if (etConfirmPas.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    etConfirmPas.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    ivEye3.setImageResource(R.drawable.ic_eye_show)
                    etConfirmPas.setSelection(etConfirmPas.text?.length ?: 0)
                } else {
                    etConfirmPas.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye3.setImageResource(R.drawable.ic_eye_hide)
                    etConfirmPas.setSelection(etConfirmPas.text?.length ?: 0)
                }
            }
            submitBtn.setOnClickListener {
                if (validator.changePasswordValidator(binding)) {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.CUR_PAS] = etCurrentPas.text?.trim()?.toString()!!
                        map[ApiConstant.NEW_PAS] = etNewPas.text?.trim()?.toString()!!
                        changePasswordApi()
                    }
                }
            }
        }
    }

}