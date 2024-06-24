package com.in2bliss.ui.activity.home.affirmation.affirmationList

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

class TextAffirmationPagingSource(
    private val apiHelperInterface: ApiHelperInterface,
    private val search: String?,
    val affirmationId: String?
) : PagingSource<Int, AffirmationListResponse.Data>() {
    override fun getRefreshKey(state: PagingState<Int, AffirmationListResponse.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AffirmationListResponse.Data> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()
            if (search.isNullOrEmpty().not()) {
                hashMap[ApiConstant.SEARCH] = search.orEmpty()
            }
            if (!affirmationId.isNullOrEmpty()){
                hashMap[ApiConstant.ID] = affirmationId
            }
            val response = apiHelperInterface.textAffirmationList(
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