package com.in2bliss.ui.activity.home.notification

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.NotificationListResponse
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

/**
 * Create by Paras on 10/04/2023
 */
class NotificationListReadPagingSource(
    private val apiHelperInterface: ApiHelperInterface,
) : PagingSource<Int, NotificationListResponse.Read>() {
    override fun getRefreshKey(state: PagingState<Int, NotificationListResponse.Read>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationListResponse.Read> {
        return try {

            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()

            val response = apiHelperInterface.getNotificationList(
                body = hashMap
            )

            val nextKey = if ((response.body()?.read?.size
                    ?: 9) % 10 != 0 || response.body()?.read?.size == 0
            ) {
                null
            } else position + 1

            LoadResult.Page(
                data = response.body()?.read ?: arrayListOf(),
                prevKey = null,
                nextKey = nextKey
            )

        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}