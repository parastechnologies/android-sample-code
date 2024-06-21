package com.highenergymind.view.sheet.subcategories

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
 * Created by developer on 05/03/24
 */
@HiltViewModel
class SubCategoryViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val apiService: ApiService
) : BaseViewModel(context) {

    val subCategoriesResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getSubCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.getSubCategory(map) }, ApiEndPoint.GET_SUB_CATEGORIES)
            subCategoriesResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}