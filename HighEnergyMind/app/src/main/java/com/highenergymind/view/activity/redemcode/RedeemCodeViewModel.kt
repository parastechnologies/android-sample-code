package com.highenergymind.view.activity.redemcode

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
 * Created by Puneet on 06/06/24
 */
@HiltViewModel
class RedeemCodeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,
    val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {
    val redeemCodeResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun redeemCodeApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.redeemCodeApi(map) }, ApiEndPoint.REDEEM_CODE)
            redeemCodeResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}