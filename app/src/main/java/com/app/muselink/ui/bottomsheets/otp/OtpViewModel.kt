package com.app.muselink.ui.bottomsheets.otp

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.responses.LoginResponse
import com.app.muselink.data.modals.responses.OtpVerificationRes
import com.app.muselink.data.modals.responses.SignUpResponse
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants

class OtpViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var code1 = ""
    var code2 = ""
    var code3 = ""
    var code4 = ""
    var credentialType = ""
    var phoneNumber = ""
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()

    //todo MutableLiveData
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private val requestApiLogin = MutableLiveData<HashMap<String, String>>()
    private val requestApiSignup = MutableLiveData<HashMap<String, String>>()

    private val _login = requestApiLogin.switchMap { requestApi ->
        repository.loginUser(requestApi)
    }
    val loginResponse: LiveData<Resource<LoginResponse>> = _login
    private val _otpVerification = requestApi.switchMap { requestApi ->
        repository.otpVerification(requestApi)
    }
    private val _signup = requestApiSignup.switchMap { requestApi ->
        repository.signUpUser(requestApi)
    }
    val signUpResponse: LiveData<Resource<SignUpResponse>> = _signup
    val verifyOtpResponse: LiveData<Resource<OtpVerificationRes>> = _otpVerification

    /**
     * Validations
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        if (code1.isEmpty() || code2.isEmpty() || code3.isEmpty() || code4.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_OTP)
        }
        return formErrors.isEmpty()
    }

    /**
     * Api call resend otp
     * */
    fun resendOtp() {
        val request = HashMap<String, String>()
        if (credentialType == SyncConstants.CredentialTypes.LOGIN.value) {
            request[SyncConstants.APIParams.PHONE.value] = phoneNumber
            request[SyncConstants.APIParams.LOGIN_TYPE.value] = SyncConstants.AuthTypes.PHONE.value
            requestApiLogin.value = request
        } else {
            request[SyncConstants.APIParams.PHONE.value] = phoneNumber
            request[SyncConstants.APIParams.SIGNUP_TYPE.value] = SyncConstants.AuthTypes.PHONE.value
            requestApiSignup.value = request
        }
    }

    /**
     * Api call otp
     * */
    fun callApiOtp() {
        if (isFormValid()) {
            val optCode = code1 + code2 + code3 + code4
            val request = HashMap<String, String>()
            request[SyncConstants.APIParams.PHONE.value] = phoneNumber
            request[SyncConstants.APIParams.OTP.value] = optCode
            request[SyncConstants.APIParams.VERIFY_TYPE.value] = credentialType
            requestApi.value = request
        }
    }
}