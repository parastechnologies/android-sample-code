package com.highenergymind.view.activity.trackDetail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ApiService
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.api.getResult
import com.highenergymind.base.BaseViewModel
import com.highenergymind.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by developer on 04/04/24
 */
@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,
    val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {

    val affirmationListResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }
    val markFavResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }
    val addRecentResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun addRecentApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult(
                    { apiService.playTrack(map) },
                    ApiEndPoint.PLAY_TRACK
                )
            addRecentResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun getTrackAffirmations() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult(
                    { apiService.getAffirmationByTrackId(map) },
                    ApiEndPoint.GET_AFFIRMATION_BY_TRACK_ID
                )
            affirmationListResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun markFav() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.markFavourite(map) }, ApiEndPoint.MARK_FAVOURITE)
            markFavResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}