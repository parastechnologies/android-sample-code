package com.in2bliss.ui.activity.home.profileDetail

import android.net.Uri
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.EditProfileResponse
import com.in2bliss.data.model.ProfileDetailResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.extension.createMultiPartForFile
import com.in2bliss.utils.extension.createMultipartForString
import com.in2bliss.utils.extension.createNullMultiPart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel
@Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {


    var tempImageUri: Uri? = null
    var imageUri: Uri? = null
    var imageFile: File? = null

    var name = ""
    var email = ""
    var message = ""
    private val mutableProfileDetailResponse by lazy {
        MutableSharedFlow<Resource<ProfileDetailResponse>>()
    }
    val profileDetailResponse by lazy { mutableProfileDetailResponse.asSharedFlow() }

    private val mutableEditProfileResponse by lazy {
        MutableSharedFlow<Resource<EditProfileResponse>>()
    }
    val editProfileResponse by lazy { mutableEditProfileResponse.asSharedFlow() }


    private val contactUsResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }

    val contactResponse by lazy { contactUsResponse.asSharedFlow() }





    /**
     * profile detail api request
     * */
    private fun getProfileDetail() {
        networkCallIo {
            mutableProfileDetailResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getProfileDetail()
                },
                apiName = ApiConstant.PROFILE_DETAIL
            )
            mutableProfileDetailResponse.emit(
                value = response
            )
        }
    }

    /**
     * edit Profile api request
     * */
    private fun editProfileDetail() {
        val filePart = imageFile?.createMultiPartForFile(
            apiKey = ApiConstant.PROFILE_PICTURE
        ) ?: createNullMultiPart(
            apiKey = ApiConstant.PROFILE_PICTURE
        )
        val hashMap = HashMap<String, RequestBody>()
        hashMap[ApiConstant.FULL_NAME] = name.createMultipartForString()

        networkCallIo {
            mutableEditProfileResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.editProfile(filePart,hashMap)
                },
                apiName = ApiConstant.PROFILE_UPDATE
            )
            mutableEditProfileResponse.emit(
                value = response
            )
        }
    }

     fun contactUs() {
        val hashMap = HashMap<String, String>()
        hashMap[ApiConstant.NAME] = name
        hashMap[ApiConstant.EMAIL] = email
        hashMap[ApiConstant.MESSAGE] = message

        networkCallIo {
            contactUsResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.contactUs(hashMap)
                },
                apiName = ApiConstant.CONTACT_US
            )
            contactUsResponse.emit(
                value = response
            )
        }
    }


    override fun retryApiRequest(apiName: String) {
        when(apiName){
            ApiConstant.PROFILE_DETAIL->getProfileDetail()
            ApiConstant.PROFILE_UPDATE->editProfileDetail()
        }

    }
}