package com.highenergymind.view.fragment.home.affirmation

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
 * Created by developer on 23/04/24
 */
@HiltViewModel
class AffirmationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService
) : BaseViewModel(context) {

    val themeResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getThemeApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult(
                    { apiService.getThemeLibrary() },
                    ApiEndPoint.GET_BACKGROUND_THEME_IMAGE_LIBRARY
                )
            themeResponse.emit(response)
            isLoading.emit(false)
        }
    }

    val affirmationScrollResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getAffirmationScrollApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.scrollAffirmation(map) }, ApiEndPoint.SCROLL_AFFIRMATION)
            affirmationScrollResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun changeBackgroundImageApi() {
        viewModelScope.launch(Dispatchers.IO) {
            getResult(
                { apiService.changeBackgroundImage(map) },
                ApiEndPoint.CHANGE_BACKGROUND_IMAGE
            )
        }
    }

    fun markFav() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.markFavourite(map) }, ApiEndPoint.MARK_FAVOURITE)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}