package com.highenergymind.view.fragment.empoweraffirmation

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
 * Created by developer on 05/03/24
 */
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val apiService: ApiService, val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {
    val categoriesResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }
    val updateCategoriesResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult({ apiService.getCategories() }, ApiEndPoint.GET_CATEGORIES)
            categoriesResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun updateCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.updateUserCategory(map) }, ApiEndPoint.UPDATE_USER_CATEGORY)
            updateCategoriesResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}