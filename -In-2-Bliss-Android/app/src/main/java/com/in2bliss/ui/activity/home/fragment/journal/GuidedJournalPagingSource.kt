package com.in2bliss.ui.activity.home.fragment.journal

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.journalList.GuidedJournalListResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

class GuidedJournalPagingSource(
    private val apiHelperInterface: ApiHelperInterface,
    private val search : String
) : PagingSource<Int, GuidedJournalListResponse.Data>() {
    override fun getRefreshKey(state: PagingState<Int, GuidedJournalListResponse.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GuidedJournalListResponse.Data> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()
            hashMap[ApiConstant.SEARCH] = search

            val response = apiHelperInterface.guidedJournal(
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