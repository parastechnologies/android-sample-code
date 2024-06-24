package com.in2bliss.ui.activity.home.notification

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.NotificationListResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class NotificationVM @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
    requestManager: RequestManager,
    imageLoader: ImageLoader,
    imageRequest: ImageRequest.Builder
) : BaseViewModel() {


    var notificationId = ""
    var isDelete = false
    var position = -1

    private val mutableReadResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val readResponse by lazy { mutableReadResponse.asSharedFlow() }

    val adapter = NotificationListAdapter()

    val adapterRead = NotificationListReadAdapter()

    fun getNotificationList(): kotlinx.coroutines.flow.Flow<PagingData<NotificationListResponse.Unread>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                NotificationListPagingSource(
                    apiHelperInterface = apiHelperInterface
                )
            }, initialKey = 0
        ).flow
    }

    fun getNotificationListRead(): kotlinx.coroutines.flow.Flow<PagingData<NotificationListResponse.Read>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                NotificationListReadPagingSource(
                    apiHelperInterface = apiHelperInterface
                )
            }, initialKey = 0
        ).flow
    }

    /**
     * read notification api request
     * */
    private fun notificationRead() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.ID] = notificationId
            mutableReadResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.readNotification(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.NOTIFICATION_READ
            )
            mutableReadResponse.emit(
                value = response
            )
        }
    }


    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.NOTIFICATION_READ -> notificationRead()
        }
    }
}

