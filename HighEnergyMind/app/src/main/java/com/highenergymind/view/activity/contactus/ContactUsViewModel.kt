package com.highenergymind.view.activity.contactus

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
 * Created by developer on 13/03/24
 */
@HiltViewModel
class ContactUsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService,
    val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {


    val contactUsResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> =
        MutableSharedFlow()

    val suggestAffirmResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> =
        MutableSharedFlow()

    fun contactUsApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                {
                    apiService.contactUsApi(map)
                }, ApiEndPoint.CONTACT_US
            )
            contactUsResponse.emit(response)
            isLoading.emit(false)
        }
    }

    fun suggestAffirmationApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                {
                    apiService.suggestAffirmation(map)
                }, ApiEndPoint.SUGGEST_AFFIRMATION
            )
            suggestAffirmResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }
}