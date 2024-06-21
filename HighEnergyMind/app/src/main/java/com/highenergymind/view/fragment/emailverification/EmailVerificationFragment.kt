package com.highenergymind.view.fragment.emailverification

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.base.BaseResponse
import com.highenergymind.databinding.FragmentEmailVerificationBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.CutCopyPasteEditText
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getCopyData
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerificationFragment : BaseFragment<FragmentEmailVerificationBinding>() {
    private val viewModel by viewModels<EmailVerificationViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_email_verification
    }

    override fun initViewWithData() {
        requireActivity().fullScreenStatusBar()
        setCollectors()
        textListeners()
        onClick()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as SignUpProcessActivity).apply {
            setProgressMeter(com.intuit.sdp.R.dimen._140sdp)
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                verifySignUpResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        sharedPrefs.setLoginStatusTrue()
                        redirectUser()
                    })
                }
            }
        }
    }

    private fun redirectUser() {
        requireContext().intentComponent(
            UnlockFeatureActivity::class.java,
            Bundle().also { bnd ->
                bnd.putString(AppConstant.IS_FROM_SIGN_UP, "")
            })
    }

    private fun onClick() {


        mBinding.verifyBtn.setOnClickListener {
            if (validator.otpValidator(mBinding)) {
                val otp = getOpt()
                viewModel.apply {
                    map.clear()
                    map[ApiConstant.EMAIL] = sharedPrefs.getUserData()?.email!!
                    map[ApiConstant.OTP] = otp
                    verifySignUp()
                }
            }
        }
    }

    private fun getOpt(): String {
        mBinding.apply {
            return "${et1.text?.trim()?.toString()}${et2.text?.trim()?.toString()}${
                et3.text?.trim()?.toString()
            }${et4.text?.trim()?.toString()}"
        }
    }

    private fun textListeners() {
        mBinding.apply {
            et1.setOnCutCopyPasteListener(object : CutCopyPasteEditText.OnCutCopyPasteListener{
                override fun onCut() {

                }

                override fun onCopy() {
                }

                override fun onPaste() {
//                    val clip=requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    val otp= clip.primaryClip?.description?.label?.toString()?:""
//                    if (otp.trim().length==4){
//                        et2.setText(otp[1].toString())
//                        et3.setText(otp[2].toString())
//                        et4.setText(otp[3].toString())
//                        et4.requestFocus()
//                    }

                }
            })
            et1.addTextChangedListener {
                it?.toString()?.trim()?.let { txt ->
                    if (txt.length == 1) {
                        val otp=requireContext().getCopyData()
                        if (otp.trim().length==4 && et1.text?.trim()?.toString()== otp[0].toString()){
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


}