package com.in2bliss.ui.activity.auth.stepThree

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class StepThreeViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val name = ObservableField("")
    var countryName:String?=null

    private val mutableAddNameResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val addNameResponse by lazy { mutableAddNameResponse.asSharedFlow() }

    private fun addName(){

        val hashMap = HashMap<String, String>()
        hashMap[ApiConstant.FULL_NAME] = name.get() ?: ""
        hashMap[ApiConstant.COUNTRY] = countryName.toString()

        networkCallIo {
            mutableAddNameResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.addName(hashMap)
                },
                apiName = ApiConstant.ADD_NAME
            )
            mutableAddNameResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when(apiName){
            ApiConstant.ADD_NAME -> addName()
        }
    }
}

