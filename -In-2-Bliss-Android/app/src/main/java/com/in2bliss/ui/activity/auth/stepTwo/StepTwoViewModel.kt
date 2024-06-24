package com.in2bliss.ui.activity.auth.stepTwo

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.LogInResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.data.google_authentication.GoogleSignInImpl
import com.in2bliss.domain.GoogleSignInInterface
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class StepTwoViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    var email = ObservableField("")
    val password = ObservableField("")
    val confirmPassword = ObservableField("")
    var socialId: String? = null
    var isLogin = false
    var unAuthorised = false
    var passwordVisible = false
    var confirmPasswordVisible = false
    var reasonId: String? = null
    var googleSignIn: GoogleSignInInterface? = null
    var deviceToken: String? = null

    private val mutableSignupSignInResponse by lazy {
        MutableSharedFlow<Resource<LogInResponse>>()
    }
    val signupSignInResponse by lazy { mutableSignupSignInResponse.asSharedFlow() }
    /**
     * SignUp api request
     * */
    private fun signUp() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.EMAIL] = email.get().orEmpty()
            hashMap[ApiConstant.PASSWORD] = password.get().orEmpty()
            hashMap[ApiConstant.REASON_ID] = reasonId.orEmpty()
            hashMap[ApiConstant.DEVICE_TOKEN] = deviceToken.orEmpty()
            hashMap[ApiConstant.DEVICE_TYPE] = AppConstant.ANDROID

            mutableSignupSignInResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.signUp(hashMap)
                },
                apiName = ApiConstant.SIGNUP
            )
            mutableSignupSignInResponse.emit(
                value = response
            )
        }
    }

    /**
     * Login api request
     * */
    private fun logIn() {

        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.EMAIL] = email.get().orEmpty()
            hashMap[ApiConstant.PASSWORD] = password.get().orEmpty()
            hashMap[ApiConstant.DEVICE_TOKEN] = deviceToken.orEmpty()
            hashMap[ApiConstant.DEVICE_TYPE] = AppConstant.ANDROID

            mutableSignupSignInResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.logIn(hashMap)
                },
                apiName = ApiConstant.LOGIN
            )
            mutableSignupSignInResponse.emit(
                value = response
            )
        }
    }

    /**
     * Social login api request
     * */
    private fun socialLogIn() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.SOCIAL_ID] = socialId.orEmpty()
            hashMap[ApiConstant.LOGIN_TYPE] = AppConstant.GMAIL
            hashMap[ApiConstant.DEVICE_TYPE] = AppConstant.ANDROID
            hashMap[ApiConstant.DEVICE_TOKEN] = deviceToken.orEmpty()
            hashMap[ApiConstant.EMAIL] = email.get().orEmpty()
            hashMap[ApiConstant.REASON_ID] = ""

            mutableSignupSignInResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.socialLogIn(hashMap)
                },
                apiName = ApiConstant.SOCIAL_LOGIN
            )
            mutableSignupSignInResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.SIGNUP -> {
                signUp()
            }

            ApiConstant.LOGIN -> {
                logIn()
            }

            ApiConstant.SOCIAL_LOGIN -> {
                socialLogIn()
            }
        }
    }

    fun initializeGoogleSignIn() {
        if (googleSignIn == null) {
            googleSignIn = getGoogleSignInInstance()
        }
    }

    private fun getGoogleSignInInstance(): GoogleSignInInterface {
        return GoogleSignInImpl()
    }
}

