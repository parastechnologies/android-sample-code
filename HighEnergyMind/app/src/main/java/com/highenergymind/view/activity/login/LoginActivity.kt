package com.highenergymind.view.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.ActivityLoginBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getAndroidID
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.forgetpass.ForgotPasswordActivity
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel by viewModels<LoginViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        getBundleData()
        onClick()
    }

    private fun getBundleData() {
        if (intent.hasExtra(ApiEndPoint.LOG_OUT)) {

            viewModel.sharedPrefs.clearPreference()
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                loginResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        sharedPrefs.saveAccessToken(response.token)
                        response.data?.let { it1 ->
                            sharedPrefs.saveUserData(it1)
                            (application as ApplicationClass).getSelectedLanguage()
                        }
                        if (response.data?.isEmailVerify == 1) {
                            sharedPrefs.setLoginStatusTrue()
                            intentComponent(HomeActivity::class.java)
                            finishAffinity()
                        } else {
                            showToast(response.data?.emailOtp)
                            intentComponent(
                                SignUpProcessActivity::class.java,
                                Bundle().also { bnd ->
                                    bnd.putString(getString(R.string.email_verification), "")
                                })
                        }
                    })
                }
            }
            lifecycleScope.launch {
                socialLoginResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        sharedPrefs.saveAccessToken(response.token)
                        response.data?.let { it1 ->
                            sharedPrefs.saveUserData(it1)
                            (application as ApplicationClass).getSelectedLanguage()

                        }
                        sharedPrefs.setLoginStatusTrue()
                        intentComponent(HomeActivity::class.java)
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
            ctlGoogle.setOnClickListener {
                viewModel.showGoogleLoginSheet(this@LoginActivity, false)
            }

            tvSignUp.setOnClickListener {
                intentComponent(SignUpProcessActivity::class.java)
            }

            binding.tvForgotPassword.setOnClickListener {
                intentComponent(ForgotPasswordActivity::class.java, null)
            }

            binding.btnLogin.setOnClickListener {

                if (validator.loginValidation(binding)) {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.EMAIL] = etEmail.text?.trim().toString()
                        map[ApiConstant.PASSWORD] = etPassword.text?.trim().toString()
                        map[ApiConstant.DEV_ID] = getAndroidID() ?: ""
                        map[ApiConstant.DEV_TYPE] = "android"
                        map[ApiConstant.DEV_TOKEN] = fireToken
                        loginApi()
                    }
                }
            }

            ivEye.setOnClickListener {
                if (etPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    ivEye.setImageResource(R.drawable.ic_eye_show)
                    etPassword.setSelection(etPassword.text?.length ?: 0)
                } else {
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    ivEye.setImageResource(R.drawable.ic_eye_hide)
                    etPassword.setSelection(etPassword.text?.length ?: 0)
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            viewModel.REQ_ONE_TAP -> {
                try {
                    val credential = viewModel.oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken

                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            viewModel.showLoader(true)
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            Firebase.auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->

                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(
                                            "TAG",
                                            "signInWithCredential:success  domain->${Firebase.auth.customAuthDomain}"
                                        )
                                        val user = Firebase.auth.currentUser
                                        hitSocialLogin(user)
                                        Firebase.auth.signOut()
                                        viewModel.oneTapClient.signOut()

                                    } else {
                                        viewModel.showLoader(false)
                                        // If sign in fails, display a message to the user.
                                        showToast(task.exception?.localizedMessage)
                                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                                    }
                                }
                            Log.d("TAG", "Got ID token. $idToken")
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d("TAG", "No ID token or password!")
                        }
                    }
                    // ...
                } catch (e: ApiException) {
                    viewModel.showLoader(false)

                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d("TAG", "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                        }

                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d("TAG", "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }

                        else -> {
                            Log.d(
                                "TAG",
                                "Couldn't get credential from result." + " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hitSocialLogin(user: FirebaseUser?) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.SOCIAL_ID] = user?.uid ?: ""
            map[ApiConstant.DEV_ID] = getAndroidID() ?: ""
            map[ApiConstant.DEV_TYPE] = "android"
            map[ApiConstant.DEV_TOKEN] = fireToken
            socialLoginApi()
        }
    }

}