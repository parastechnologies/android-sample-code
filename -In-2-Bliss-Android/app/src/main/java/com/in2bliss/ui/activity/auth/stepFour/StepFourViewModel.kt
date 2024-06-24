package com.in2bliss.ui.activity.auth.stepFour

import android.net.Uri
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.ProfileResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.extension.createMultiPartForFile
import com.in2bliss.utils.extension.createNullMultiPart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StepFourViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    var tempImageUri: Uri? = null
    var imageUri: Uri? = null
    var imageFile: File? = null

    private val mutableAddProfileResponse by lazy {
        MutableSharedFlow<Resource<ProfileResponse>>()
    }
    val addProfileResponse by lazy { mutableAddProfileResponse.asSharedFlow() }

    private fun addProfile() {
        val filePart = imageFile?.createMultiPartForFile(
            apiKey = ApiConstant.PROFILE_PICTURE
        ) ?: createNullMultiPart(
            apiKey = ApiConstant.PROFILE_PICTURE
        )
        networkCallIo {
            mutableAddProfileResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.addProfilePicture(filePart)
                },
                apiName = ApiConstant.PICTURE_ADD
            )
            mutableAddProfileResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.PICTURE_ADD -> addProfile()
        }
    }
}

