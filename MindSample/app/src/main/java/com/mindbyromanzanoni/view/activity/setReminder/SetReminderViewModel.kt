package com.mindbyromanzanoni.view.activity.setReminder

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.Response
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseResponse
import com.mindbyromanzanoni.base.BaseViewModel
import com.mindbyromanzanoni.data.response.CommonResponse
import com.mindbyromanzanoni.data.response.reminder.GetReminderResponseModel
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.utils.constant.ApiConstants
import com.mindbyromanzanoni.utils.showErrorBarAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class SetReminderViewModel @Inject constructor(private var repeatSourceImp: RepeatSourceImp) :
    BaseViewModel() {
    private val _repeatReminderResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val repeatReminderResponse = _repeatReminderResponse.asSharedFlow()

    private val _reminderListResponse: MutableSharedFlow<Resource<GetReminderResponseModel>> = MutableSharedFlow()
    val reminderListResponse = _reminderListResponse.asSharedFlow()

    private val _deleteReminderResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val deleteReminderResponse = _deleteReminderResponse.asSharedFlow()
      fun hitReminderApi(hashMap: HashMap<String, Any?>) {
        viewModelScope.launch {
            showLoading.value=true
            repeatSourceImp.executeAddReminderApi(hashMap, ApiConstants.ADD_REMINDER).catch { e ->
                _repeatReminderResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                _repeatReminderResponse.emit(isResponse)
            }
        }
    }
    fun hitGetReminderListApi() {
        viewModelScope.launch {
            showLoading.value=true
            repeatSourceImp.executeReminderListApi(ApiConstants.GET_REMINDER).catch { e ->
                _reminderListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                _reminderListResponse.emit(isResponse)
            }
        }
    }
        fun hitDeleteReminderListApi(hashMap: HashMap<String, Any?>) {
        viewModelScope.launch {
            showLoading.value=true
            repeatSourceImp.executeDeleteReminderApi(hashMap,ApiConstants.DELETE_REMINDER).catch { e ->
                _deleteReminderResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                _deleteReminderResponse.emit(isResponse)
            }
        }
    }
    fun validations(activity: Activity, model: SetReminderModel): Boolean {
        return when {
            model.isTimeValid -> {
                showErrorBarAlert(
                    activity,
                    activity.getString(R.string.empty),
                    activity.getString(R.string.please_enter_time)
                )
                false
            }

            model.isLabelEmpty -> {
                showErrorBarAlert(
                    activity,
                    activity.getString(R.string.empty),
                    activity.getString(R.string.please_enter_label)
                )
                false
            }

            else -> {
                true
            }
        }
    }
}