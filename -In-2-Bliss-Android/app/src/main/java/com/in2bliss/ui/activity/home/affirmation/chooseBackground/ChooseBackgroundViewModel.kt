package com.in2bliss.ui.activity.home.affirmation.chooseBackground

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.ChooseBackgroundResponse
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.uploadData.Upload
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.textback.JournalBackgroundPagingSource
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.textback.TextBackPagingSource
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.createMultiPartForFile
import com.in2bliss.utils.extension.createMultipartForString
import com.in2bliss.utils.extension.createNullMultiPart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChooseBackgroundViewModel @Inject constructor(
     val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    var categoryType: AppConstant.HomeCategory? = null
    var isGalleryImageSelected = false
    var tempImageUri: Uri? = null

    var selectedImage: String? = null
    var initialSelectedImage : String? = null
    var imageUri: Uri? = null
    var imageFile: File? = null
    var createAffirmation: CreateAffirmation? = null

    private val mutableChooseBackgroundResponse by lazy {
        MutableSharedFlow<Resource<ChooseBackgroundResponse>>()
    }
    val chooseBackgroundInResponse by lazy { mutableChooseBackgroundResponse.asSharedFlow() }

    private val mutableUploadData by lazy {
        MutableSharedFlow<Resource<Upload>>()
    }


    val uploadData by lazy { mutableUploadData.asSharedFlow() }

    val adapterChooseBackground by lazy {
        ChooseBackgroundAdapter(
            requestManager = requestManager,
            categoryType = categoryType
        )
    }

/*    private fun backgroundIMages() {
        networkCallIo {
            mutableChooseBackgroundResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    when (categoryType) {
                        AppConstant.HomeCategory.JOURNAL -> apiHelperInterface.chooseJournalBackground()
                        else -> apiHelperInterface.chooseJournalBackground()
                    }
                },
                apiName = when (categoryType) {
                    AppConstant.HomeCategory.JOURNAL -> ApiConstant.JOURNAL_BACKGROUND_IMAGES
                    else -> ApiConstant.AFFIRMATION_BACKGROUND_IMAGES
                }
            )
            mutableChooseBackgroundResponse.emit(
                value = response
            )
        }
    }*/


    /**
     * Getting text affirmation background paging data
     * */
    fun getJournalBackground(): Flow<PagingData<ChooseBackgroundResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                JournalBackgroundPagingSource(
                    apiHelperInterface = apiHelperInterface,
                )
            },
            initialKey = 0
        ).flow
    }



    fun getAffirmationBackground(): Flow<PagingData<ChooseBackgroundResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TextBackPagingSource(
                    apiHelperInterface = apiHelperInterface,
                )
            },
            initialKey = 0
        ).flow
    }





    private fun upload() {
        networkCallIo {
            val filePart = imageFile.createMultiPartForFile(
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

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
//            ApiConstant.JOURNAL_BACKGROUND_IMAGES -> backgroundIMages()
//            ApiConstant.AFFIRMATION_BACKGROUND_IMAGES -> backgroundIMages()
            ApiConstant.UPLOAD -> upload()
        }
    }
}