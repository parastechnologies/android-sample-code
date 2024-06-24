package com.in2bliss.ui.activity.home

import android.util.Base64
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.GetShareDataResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.UnsupportedEncodingException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val apiHelperInterface: ApiHelperInterface) :
    BaseViewModel() {

    var id: String? = null
    var type: String? = null

    private val mutableSharedData by lazy {
        MutableSharedFlow<Resource<GetShareDataResponse>>()
    }
    val sharedData by lazy { mutableSharedData.asSharedFlow() }


    private fun getUrlData() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.TYPE] = type.orEmpty()
            hashMap[ApiConstant.ID] = id.orEmpty()
            mutableSharedData.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getSharedData(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.SHARE_DATA
            )
            mutableSharedData.emit(
                value = response
            )
        }
    }





    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.SHARE_DATA -> getUrlData()
        }
    }

    fun getCategoryType(
    ): AppConstant.HomeCategory {
        return when (decodeBase64(type ?: "")) {
            "0" -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
            "1" -> AppConstant.HomeCategory.GUIDED_MEDITATION
            "2" -> AppConstant.HomeCategory.MUSIC
            "3" -> AppConstant.HomeCategory.WISDOM_INSPIRATION
            else -> AppConstant.HomeCategory.TEXT_AFFIRMATION
        }
    }

    /**
     * Decrypting the data
     * @param coded
     * */
    fun decodeBase64(coded: String): String {
        var valueDecoded = ByteArray(0)
        try {
            valueDecoded = Base64.decode(coded.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return String(valueDecoded)
    }
}