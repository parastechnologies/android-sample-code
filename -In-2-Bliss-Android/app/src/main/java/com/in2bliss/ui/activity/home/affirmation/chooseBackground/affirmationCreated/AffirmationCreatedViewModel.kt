package com.in2bliss.ui.activity.home.affirmation.chooseBackground.affirmationCreated

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.uploadData.Upload
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
class AffirmationCreatedViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val title = ObservableField("")
    val description = ObservableField("")

    var audioUrl: String? = null
    var audioFile: File? = null
    var createAffirmation: CreateAffirmation? = null

    private val mutableCreateAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val createAffirmationResponse by lazy {
        mutableCreateAffirmation.asSharedFlow()
    }

    private val mutableUploadData by lazy {
        MutableSharedFlow<Resource<Upload>>()
    }
    val uploadData by lazy { mutableUploadData.asSharedFlow() }

    private fun upload() {
        networkCallIo {

            val filePart = audioFile.createMultiPartForFile(
                apiKey = ApiConstant.FILE
            ) ?: createNullMultiPart(
                apiKey = ApiConstant.FILE
            )
            val requestBody = HashMap<String, RequestBody>()
            val type = ApiConstant.UploadType.AFFIRMATIONS.value
            requestBody[ApiConstant.TYPE] = type.createMultipartForString()

            mutableUploadData.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.upLoadData(
                        multipartBody = filePart,
                        body = requestBody
                    )
                },
                apiName = ApiConstant.UPLOAD
            )
            mutableUploadData.emit(value = response)
        }
    }

    /**
     * Creating and updating the affirmation
     * */
    private fun createUpdateAffirmation() {
        networkCallIo {

            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.TYPE] = "1"
            hashMap[ApiConstant.TITLE] = createAffirmation?.affirmationTitle.orEmpty()
            hashMap[ApiConstant.DESCRIPTION] = createAffirmation?.affirmationDetail.orEmpty()
            hashMap[ApiConstant.THUMBNAIL] = createAffirmation?.affirmationBackground.orEmpty()
            hashMap[ApiConstant.BACKGROUND] = createAffirmation?.backgroundMusic.orEmpty()
            hashMap[ApiConstant.AUDIO_NAME] = createAffirmation?.audioName.orEmpty()

            if (createAffirmation?.audioType != null) {
                hashMap[ApiConstant.AUDIO_TYPE] = createAffirmation?.audioType?.value.orEmpty()
            }
            if (createAffirmation?.transcript != null) {
                hashMap[ApiConstant.TRANSCRIPT] = createAffirmation?.transcript.orEmpty()
            }
            if (audioUrl != null) {
                hashMap[ApiConstant.AUDIO] = audioUrl.orEmpty()
            }
            if (createAffirmation?.audioDuration != null) {
                hashMap[ApiConstant.DURATION] = createAffirmation?.audioDuration.toString()
            }

            if (createAffirmation?.isEdit == true) {
                hashMap[ApiConstant.AFFIRMATION_ID] = createAffirmation?.affirmationId.toString()
            }

            mutableCreateAffirmation.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    if (createAffirmation?.isEdit == true) {
                        apiHelperInterface.updateSpokenAffirmation(
                            body = hashMap,
                        )
                    } else apiHelperInterface.addSpokenAffirmation(
                        body = hashMap,
                    )
                },
                apiName = ApiConstant.FULL_NAME
            )
            mutableCreateAffirmation.emit(value = response)
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.ADD_AFFIRMATION -> createUpdateAffirmation()
            ApiConstant.AFFIRMATION_UPDATE -> createUpdateAffirmation()
            ApiConstant.UPLOAD -> upload()
        }
    }
}