package com.highenergymind.view.activity.forgotOtpVerification

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ApiService
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.api.getResult
import com.highenergymind.base.BaseViewModel
import com.highenergymind.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by developer on 11/03/24
 */
@HiltViewModel
class ForgotOtpVerificationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {

    val resendForgotPasswordResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> =
        MutableSharedFlow()
    val verifyOtpResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }


    /**
     * Login api
     */
    fun resendForgotPasswordApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                {
                    apiService.forgotPassword(map)
                }, ApiEndPoint.FORGOT_PASSWORD
            )
            resendForgotPasswordResponse.emit(response)
            isLoading.emit(false)
        }
    }



    fun verityOtp() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.verifySignUpOtp(map) }, ApiEndPoint.VERIFY_SIGN_UP_OTP)
            verifyOtpResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}