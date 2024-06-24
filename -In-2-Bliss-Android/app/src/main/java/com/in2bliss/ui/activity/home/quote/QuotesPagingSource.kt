package com.in2bliss.ui.activity.home.quote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.QuotesResponseModel
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

class QuotesPagingSource(
    private val apiHelperInterface: ApiHelperInterface, var id: String? = null
) : PagingSource<Int, QuotesResponseModel.Data>() {

    override fun getRefreshKey(state: PagingState<Int, QuotesResponseModel.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuotesResponseModel.Data> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()
            id?.let {
                hashMap[ApiConstant.ID] = it
            }
            val response = apiHelperInterface.quotesList(body = hashMap)

            val nextKey = if (((response.body()?.data?.size
                    ?: 9) % 10 != 0) || response.body()?.data?.size == 0
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