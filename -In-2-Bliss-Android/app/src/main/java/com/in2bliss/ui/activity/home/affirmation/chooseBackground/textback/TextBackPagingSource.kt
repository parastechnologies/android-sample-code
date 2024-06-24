package com.in2bliss.ui.activity.home.affirmation.chooseBackground.textback

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.data.model.ChooseBackgroundResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

class TextBackPagingSource(
    private val apiHelperInterface: ApiHelperInterface
) : PagingSource<Int, ChooseBackgroundResponse.Data>() {
    override fun getRefreshKey(state: PagingState<Int, ChooseBackgroundResponse.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChooseBackgroundResponse.Data> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()

            val response = apiHelperInterface.chooseAffirmationBackground(
                 hashMap
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