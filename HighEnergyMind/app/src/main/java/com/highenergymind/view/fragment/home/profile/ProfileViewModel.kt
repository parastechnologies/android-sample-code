package com.highenergymind.view.fragment.home.profile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ApiService
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.api.getResult
import com.highenergymind.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by developer on 08/03/24
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,

    ) : BaseViewModel(context) {

    val profileResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }
    val logOutResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }
    val deleteAccountResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }


    fun getProfileApi() {
        viewModelScope.launch(Dispatchers.IO) {
//            isLoading.emit(true)
            val response = getResult({ apiService.getProfile() }, ApiEndPoint.GET_PROFILE)
            profileResponse.emit(response)
//            isLoading.emit(false)
        }
    }


    fun logOutApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.logOut() }, ApiEndPoint.LOG_OUT)
            logOutResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun deleteAccountApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.deleteAccountApi() }, ApiEndPoint.DELETE_ACCOUNT)
            deleteAccountResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }

}