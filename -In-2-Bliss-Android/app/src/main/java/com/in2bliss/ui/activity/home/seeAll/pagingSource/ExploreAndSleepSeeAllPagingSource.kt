package com.in2bliss.ui.activity.home.seeAll.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant

/**
 * Create by Paras on 10/04/2023
 */
class ExploreAndSleepSeeAllPagingSource(
    private val type: String? = null,
    private val dataType: String? = null,
    private val search: String? = null,
    private val apiType : AppConstant.SeeAllType? = null,
    private val apiHelperInterface: ApiHelperInterface
) : PagingSource<Int, SeeAllResponse.Data>() {

    override fun getRefreshKey(state: PagingState<Int, SeeAllResponse.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SeeAllResponse.Data> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()

            val response = if (apiType == AppConstant.SeeAllType.EXPLORE) {
                hashMap[ApiConstant.TYPE] = type.orEmpty()
                hashMap[ApiConstant.DATA_TYPE] = dataType.orEmpty()
                hashMap[ApiConstant.SEARCH] = search.orEmpty()
                apiHelperInterface.exploreSeeALl(
                    body = hashMap
                )
            } else {
                hashMap[ApiConstant.TYPE] = dataType.orEmpty()
                apiHelperInterface.sleepSeeALl(
                    body = hashMap
                )
            }

            val nextKey = if ((response.body()?.data?.size
                    ?: 9) % 10 != 0 || response.body()?.data?.size == 0
            ) {
                null
            } else position + 1

            LoadResult.Page(
                data = response.body()?.data ?: arrayListOf(),
                prevKey = null,
                nextKey = nextKey
            )

        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}