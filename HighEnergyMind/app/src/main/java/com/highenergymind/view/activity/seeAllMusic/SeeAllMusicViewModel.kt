package com.highenergymind.view.activity.seeAllMusic

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.highenergymind.api.ApiService
import com.highenergymind.base.BaseViewModel
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.view.adapter.pagingSource.MusicPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Created by Puneet on 15/05/24
 */
@HiltViewModel
class SeeAllMusicViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {

    fun getSeeAllMusicLib(id: String, keyword: String) = Pager(config = PagingConfig(pageSize = 10)) {
        MusicPagingSource(id, apiService,keyword)
    }.flow.cachedIn(viewModelScope)

    override fun retry(type: String) {

    }
}