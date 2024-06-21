package com.highenergymind.view.fragment.signup

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.FragmentSignUpBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getAndroidID
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.login.LoginActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessViewModel
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_sign_up
    }

    override fun initViewWithData() {
        requireActivity().fullScreenStatusBar()
        setCollectors()
        setData()
        onClick()
    }

    private fun setCollectors() {
        activityViewModels<SignUpProcessViewModel>().value.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                registerResponse.collectLatest {
                    handleResponse(it, { res ->

                        val response = res as UserResponse
                        sharedPrefs.saveAccessToken(response.token)
                        response.data?.let { it1 -> sharedPrefs.saveUserData(it1) }
                        requireContext().showToast(response.data?.emailOtp)
                        view?.let { view ->
                            Navigation.findNavController(view)
                                .navigate(R.id.action_signUp_to_emailVerification)
                        }

                    })
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                socialSignUpResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        sharedPrefs.saveAccessToken(response.token)
                        response.data?.let { it1 -> sharedPrefs.saveUserData(it1) }
                        redirectUser()
                    })
                }
            }
        }
    }

    private fun setData() {
        activityViewModels<SignUpProcessViewModel>().value.apply {
            mBinding.welComeTV.text = mBinding.welComeTV.text.toString().replace("Anna", map[ApiConstant.NAME].toString())
        }
    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as SignUpProcessActivity).apply {
            setProgressMeter(com.intuit.sdp.R.dimen._140sdp)
        }
    }

    private fun onClick() {
        mBinding.apply {
            googleLogin.setOnClickListener {
                activityViewModels<SignUpProcessViewModel>().value.showGoogleLoginSheet(
                    requireActivity(),
                    false
                )
            }
            tvSignIn.setOnClickListener {
                requireContext().intentComponent(LoginActivity::class.java)
            }

            ivEye1.setOnClickListener {
                if (passwordEdt.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    passwordEdt.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    ivEye1.setImageResource(R.drawable.ic_eye_show)
                    passwordEdt.setSelection(passwordEdt.text?.length ?: 0)
                } else {
                    passwordEdt.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye1.setImageResource(R.drawable.ic_eye_hide)
                    passwordEdt.setSelection(passwordEdt.text?.length ?: 0)
                }
            }

            ivEye2.setOnClickListener {
                if (passwordEdtCon.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    passwordEdtCon.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    ivEye2.setImageResource(R.drawable.ic_eye_show)
                    passwordEdtCon.setSelection(passwordEdtCon.text?.length ?: 0)
                } else {
                    passwordEdtCon.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye2.setImageResource(R.drawable.ic_eye_hide)
                    passwordEdtCon.setSelection(passwordEdtCon.text?.length ?: 0)
                }
            }
            mBinding.signupBtn.setOnClickListener {
                if (validator.signUpValidation(mBinding)) {
                    activityViewModels<SignUpProcessViewModel>().value.apply {
                        map[ApiConstant.EMAIL] = emailEdt.text?.trim()?.toString() ?: ""
                        map[ApiConstant.PASSWORD] = passwordEdt.text?.trim()?.toString() ?: ""
                        map[ApiConstant.DEV_ID] = requireActivity().getAndroidID()?:""
                        map[ApiConstant.DEV_TYPE] = "android"
                        map[ApiConstant.DEV_TOKEN] = fireToken
                        registerApi()
                    }
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

}