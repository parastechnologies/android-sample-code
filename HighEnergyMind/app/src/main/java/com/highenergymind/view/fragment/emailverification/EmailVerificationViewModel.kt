package com.highenergymind.view.fragment.emailverification

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
 * Created by developer on 07/03/24

 */
@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val apiService: ApiService,val  sharedPrefs: SharedPrefs
) :BaseViewModel(context){
    val verifySignUpResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun verifySignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.verifySignUpOtp(map) }, ApiEndPoint.VERIFY_SIGN_UP_OTP)
            verifySignUpResponse.emit(response)
            isLoading.emit(false)
        }
    }
    override fun retry(type: String) {

    }
}