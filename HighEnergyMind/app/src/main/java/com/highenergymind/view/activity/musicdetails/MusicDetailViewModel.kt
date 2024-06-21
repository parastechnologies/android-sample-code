package com.highenergymind.view.activity.musicdetails

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
 * Created by Puneet on 03/06/24
 */
@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService
) : BaseViewModel(context) {

    val musicDetailResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getMusicDetailApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.getMusicDetailApi(map) }, ApiEndPoint.GET_MUSIC_DETAIL)
            musicDetailResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}