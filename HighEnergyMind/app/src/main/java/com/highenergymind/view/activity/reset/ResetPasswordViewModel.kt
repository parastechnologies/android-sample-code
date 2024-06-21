package com.highenergymind.view.activity.reset

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
class ResetPasswordViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,
    val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {

    val resetPasswordResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> =
        MutableSharedFlow()


    /**
     * reset password api
     */
    fun resetPasswordApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                {
                    apiService.resetPassword(map)
                }, ApiEndPoint.RESET_PASSWORD
            )
            resetPasswordResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}