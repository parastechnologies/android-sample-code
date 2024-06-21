package com.highenergymind.view.activity.notification

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
 * Created by developer on 30/04/24
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService
) : BaseViewModel(context) {

    val notificationResponse by lazy {
        MutableSharedFlow<ResponseResult<ResponseWrapper>>()
    }


    fun getNotificationList() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.getNotificationList(map)}, ApiEndPoint.GET_NOTIFICATION_LIST)
            notificationResponse.emit(response)
            isLoading.emit(false)

        }
    }

    override fun retry(type: String) {

    }
}