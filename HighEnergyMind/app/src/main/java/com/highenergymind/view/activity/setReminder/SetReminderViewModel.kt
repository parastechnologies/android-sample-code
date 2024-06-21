package com.highenergymind.view.activity.setReminder

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
 * Created by developer on 11/03/24
 */
@HiltViewModel
class SetReminderViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService
) :BaseViewModel(context){

    val setReminderResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> = MutableSharedFlow()
    val getReminderResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> = MutableSharedFlow()


    /**
     * Login api
     */
    fun setReminderApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                { apiService.addReminder(map)
                }, ApiEndPoint.ADD_REMINDER
            )
            setReminderResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun getReminderApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                { apiService.getReminder()
                }, ApiEndPoint.GET_REMINDER
            )
            getReminderResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}