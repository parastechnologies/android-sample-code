package com.highenergymind.view.sheet.managecategories

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
 * Created by developer on 09/04/24
 */
@HiltViewModel
class ManageCategoryViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService
) : BaseViewModel(context) {

    val delSubResponse by lazy {
        MutableSharedFlow<ResponseResult<ResponseWrapper>>()
    }

    fun delSubApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.deleteSubCategory(map) }, ApiEndPoint.DEL_SUB_CATEGORY)
            delSubResponse.emit(response)

            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}