package com.in2bliss.ui.activity.home.profileManagement.manageNotification

import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.GetNotificationStatusResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ManageNotificationVM @Inject constructor(val apiHelperInterface: ApiHelperInterface) :
    BaseViewModel() {

    private val mutableGetNotificationResponse by lazy {
        MutableSharedFlow<Resource<GetNotificationStatusResponse>>()
    }

    val getNotificationResponse by lazy { mutableGetNotificationResponse.asSharedFlow() }

    private val mutableSetNotificationResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }

    val setNotificationResponse by lazy { mutableSetNotificationResponse.asSharedFlow() }

    var status = ""
    var type = ""


    /**
     * notification status api request
     * */
    private fun getNotificationStatus() {
        networkCallIo {
            mutableGetNotificationResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getNotificationStatus()
                },
                apiName = ApiConstant.PROFILE_UPDATE
            )
            mutableGetNotificationResponse.emit(
                value = response
            )
        }
    }

    private fun setNotificationStatus() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.STATUS] = status
            hashMap[ApiConstant.TYPE] = type
            mutableSetNotificationResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.setNotificationStatus(hashMap)
                },
                apiName = ApiConstant.PROFILE_UPDATE
            )
            mutableSetNotificationResponse.emit(
                value = response
            )
        }
    }


    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.NOTIFICATION_STATUS -> getNotificationStatus()
            ApiConstant.NOTIFICATION_STATUS_SET -> setNotificationStatus()
        }
    }
}