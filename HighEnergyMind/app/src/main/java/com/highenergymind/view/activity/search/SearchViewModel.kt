package com.highenergymind.view.activity.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ApiService
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.api.getResult
import com.highenergymind.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by developer on 17/04/24
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService
) : BaseViewModel(context) {
    val searchResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getSearchApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult(
                    { apiService.searchAffirmationTrack(map) },
                    ApiEndPoint.SEARCH_AFFIRMATION_TRACK
                )
            searchResponse.emit(response)
            isLoading.emit(false)
        }
    }


    fun markFav() {
        viewModelScope.launch(Dispatchers.IO) {
            getResult({ apiService.markFavourite(map) }, ApiEndPoint.MARK_FAVOURITE)

        }
    }
    override fun retry(type: String) {

    }
}