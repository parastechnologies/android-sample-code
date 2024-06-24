package com.in2bliss.ui.activity.home.profileManagement.favourites

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.FavouritesResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

/**
 * Create by Paras on 11/01/2023
 */
class FavouritePagingSource(
    private val apiHelperInterface: ApiHelperInterface,
    private val favouritesType: String,
    private val type: String,
) :
    PagingSource<Int, FavouritesResponse.Data>() {
    override fun getRefreshKey(state: PagingState<Int, FavouritesResponse.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FavouritesResponse.Data> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] = favouritesType
            hashMap[ApiConstant.DATA_TYPE] = type
            val response = apiHelperInterface.favourite(
                body = hashMap
            )

            val nextKey = if ((response.body()?.data?.size ?: 9) % 10 != 0) {
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