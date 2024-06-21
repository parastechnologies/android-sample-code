package com.highenergymind.view.activity.forgotOtpVerification

import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.ActivityEmailVarificationBinding
import com.highenergymind.utils.CutCopyPasteEditText
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getCopyData
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.reset.ResetPasswordActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotOtpVerificationActivity : BaseActivity<ActivityEmailVarificationBinding>() {
    val viewModel by viewModels<ForgotOtpVerificationViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_email_varification
    }

    override fun initView() {
        fullScreenStatusBar()
        onClick()
        setCollectors()
        textListeners()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                resendForgotPasswordResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        showToast(response.message)
                        showToast(response.data?.emailOtp)

                    })
                }
            }
            lifecycleScope.launch {
                verifyOtpResponse.collectLatest {
                    handleResponse(it, { resp ->
                        val response = resp as BaseResponse
                        showToast(response.message)
                        intentComponent(ResetPasswordActivity::class.java)
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
            tvResend.setOnClickListener {
                viewModel.apply {
                    map.clear()
                    map[ApiConstant.EMAIL] = sharedPrefs.getUserData()?.email!!
                    map[ApiConstant.TYPE] = "2" /* 2 means resend otp type */
                    resendForgotPasswordApi()
                }
            }
            verifyBtn.setOnClickListener {

                if (validator.otpValidator(binding)) {
                    val otp = getOpt()
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.EMAIL] = sharedPrefs.getUserData()?.email!!
                        map[ApiConstant.OTP] = otp
                        verityOtp()
                    }
                }
            }
        }

        binding.customTool.backIV.setOnClickListener {
            finish()
        }

    }

    private fun textListeners() {
        binding.apply {


            et1.addTextChangedListener {
                it?.toString()?.trim()?.let { txt ->
                    if (txt.length == 1) {
                        val otp=getCopyData()
                        if (otp.trim().length==4 && et1.text?.trim()?.toString()== getCopyData()[0].toString()){

                            et2.setText(otp[1].toString())
                            et3.setText(otp[2].toString())
                            et4.setText(otp[3].toString())
                            et4.requestFocus()
                            et4.setSelection(et4.text.length)
                            et3.setSelection(et3.text.length)
                            et2.setSelection(et2.text.length)
                            et1.setSelection(et1.text.length)

                        }else {
                            et2.requestFocus()
                        }
                    }
                }
            }
            et2.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (et2.text?.trim()?.toString().isNullOrEmpty()) {
                        et1.requestFocus()
                    }
                }
                false
            }
            et2.addTextChangedListener {
                it?.toString()?.trim()?.let { txt ->
                    if (txt.length == 1) {
                        et3.requestFocus()
                    }
                }
            }
            et3.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (et3.text?.trim()?.toString().isNullOrEmpty()) {
                        et2.requestFocus()
                    }
                }
                false
            }
            et3.addTextChangedListener {
                it?.toString()?.trim()?.let { txt ->
                    if (txt.length == 1) {
                        et4.requestFocus()
                    }
                }
            }
            et4.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (et4.text?.trim()?.toString().isNullOrEmpty()) {
                        et3.requestFocus()
                    }
                }
                false
            }


        }
    }

    private fun getOpt(): String {
        binding.apply {
            return "${et1.text?.trim()?.toString()}${et2.text?.trim()?.toString()}${
                et3.text?.trim()?.toString()
            }${et4.text?.trim()?.toString()}"
        }
    }

}