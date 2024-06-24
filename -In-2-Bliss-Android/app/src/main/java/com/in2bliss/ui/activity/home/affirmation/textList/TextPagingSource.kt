package com.in2bliss.ui.activity.home.affirmation.textList

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.TextAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface

class TextPagingSource(
    private val apiHelperInterface: ApiHelperInterface,
    private val id: String?,
    private var onItemEmpty: (() -> Unit)? = null
) : PagingSource<Int, TextAffirmation>() {
    override fun getRefreshKey(state: PagingState<Int, TextAffirmation>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TextAffirmation> {
        return try {
            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()
            hashMap[ApiConstant.ID] = id.toString()


            val response = apiHelperInterface.myTextAffirmationList(
                body = hashMap
            )

            if (position == 0 && response.body()?.data.isNullOrEmpty()) {
                onItemEmpty?.invoke()
            }

            val nextKey = if (response.body()?.data.isNullOrEmpty() && (response.body()?.data?.size ?: 9) % 10 != 0) {
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