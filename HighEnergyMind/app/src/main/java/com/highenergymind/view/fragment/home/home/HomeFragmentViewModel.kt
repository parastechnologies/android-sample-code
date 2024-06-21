package com.highenergymind.view.fragment.home.home

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
 * Created by developer on 08/03/24
 */
@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,
    val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {

    val homeResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }
    val markFavResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getHomeDashboardApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.homeDashboardApi() }, ApiEndPoint.HOME_DASHBOARD)
            homeResponse.emit(response)
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