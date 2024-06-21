package com.highenergymind.view.sheet.trackmusicsheet

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
 * Created by developer on 17/04/24
 */
@HiltViewModel
class TrackMusicViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {

    val musicResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getMusicApi() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult(
                    { apiService.getMusicLibrary(map) },
                    ApiEndPoint.GET_BACKGROUND_AUDIO_LIBRARY
                )
            musicResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}