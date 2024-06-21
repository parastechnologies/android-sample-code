package com.highenergymind.view.adapter.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.highenergymind.api.ApiConstant
import com.highenergymind.api.ApiService
import com.highenergymind.data.BackAudios


/**
 * Created by Puneet on 15/05/24
 */
class MusicPagingSource(val catId: String, val apiService: ApiService, val keyword: String) :
    PagingSource<Int, BackAudios>() {
    private val limit = 10
    var offset = 0

    override fun getRefreshKey(state: PagingState<Int, BackAudios>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BackAudios> {
        offset = params.key ?: 0

        return try {
            val map = HashMap<String, Any>()
            map[ApiConstant.MUSIC_ID] = catId
            map[ApiConstant.LIMIT] = limit.toString()
            map[ApiConstant.OFFSET] = offset
            map[ApiConstant.KEYWORD] = keyword
            val response = apiService.seeAllMusicApi(map)
            val list = response.body()?.data ?: mutableListOf()
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = if (list.size >= limit) list.size - 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}