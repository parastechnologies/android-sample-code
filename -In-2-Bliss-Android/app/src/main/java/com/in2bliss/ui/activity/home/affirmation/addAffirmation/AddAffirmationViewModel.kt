package com.in2bliss.ui.activity.home.affirmation.addAffirmation

import android.net.Uri
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.data.model.uploadData.Upload
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.createMultiPartForFile
import com.in2bliss.utils.extension.createMultipartForString
import com.in2bliss.utils.extension.createNullMultiPart
import com.in2bliss.utils.extension.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddAffirmationViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val affirmation = ObservableField("")
    val title = ObservableField("")
    val textCount = ObservableField("0/100")
    val createAffirmationCount = ObservableField("")
    val journalDescription = ObservableField("I am so grateful for ")
    val createAffirmation = ObservableField("")
    val transcript = ObservableField("")
    val transcriptCount = ObservableField("0/1000")
    var tempImageUri: Uri? = null
    var imageUri: Uri? = null
    var imageFile: File? = null
    var categoryType: AppConstant.HomeCategory? = null
    var isTranscript = false
    var backgroundImage: String? = null
    var date: String? = null
    var journalData: JournalDetail? = null
    var textAffirmationData: AffirmationListResponse.Data? = null
    var isAdminImageSelected = false
    var isEdit = false
    var defaultTextForJournal = "I am so grateful for "
    var createAffirmationDetails: CreateAffirmation? = null

    private val mutableUploadData by lazy {
        MutableSharedFlow<Resource<Upload>>()
    }
    val uploadData by lazy { mutableUploadData.asSharedFlow() }

    private val mutableAddGuidedJournal by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val guidedJournalData by lazy { mutableAddGuidedJournal.asSharedFlow() }

    private val mutableEditJournal by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val editJournal by lazy {
        mutableEditJournal.asSharedFlow()
    }

    private fun uploadImage() {
        networkCallIo {
            val imagePart = imageFile.createMultiPartForFile(
                apiKey = ApiConstant.FILE
            ) ?: createNullMultiPart(
                apiKey = ApiConstant.FILE
            )
            val requestBody = HashMap<String, RequestBody>()

            val type = when (categoryType) {
                AppConstant.HomeCategory.JOURNAL -> ApiConstant.UploadType.JOURNAL.value.createMultipartForString()
                else -> ApiConstant.UploadType.AFFIRMATIONS.value.createMultipartForString()
            }
            requestBody[ApiConstant.TYPE] = type

            mutableUploadData.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.upLoadData(
                        multipartBody = imagePart,
                        body = requestBody
                    )
                },
                apiName = ApiConstant.UPLOAD
            )
            mutableUploadData.emit(value = response)
        }
    }

    private fun addGuidedJournal() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.DESCRIPTION] = journalDescription.get().orEmpty()
            hashMap[ApiConstant.BACKGROUND] = backgroundImage.orEmpty()
            hashMap[ApiConstant.DATE] = date ?: getCurrentDate()

            mutableAddGuidedJournal.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.addGuidedJournal(hashMap)
                },
                apiName = ApiConstant.JOURNAL_ADD
            )
            mutableAddGuidedJournal.emit(
                value = response
            )
        }
    }

    private fun editJournal() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.DESCRIPTION] = journalDescription.get().orEmpty()
            hashMap[ApiConstant.ID] = journalData?.id.orEmpty()
            hashMap[ApiConstant.BACKGROUND] = backgroundImage.orEmpty()

            mutableEditJournal.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.editJournal(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.EDIT_JOURNAL
            )
            mutableEditJournal.emit(value = response)
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.UPLOAD -> uploadImage()
            ApiConstant.JOURNAL_ADD -> addGuidedJournal()
            ApiConstant.EDIT_JOURNAL -> editJournal()
        }
    }

    fun getCategoryAndSubCategory(): String {
        val categoryList = mutableListOf<HashMap<String, Any>>()
        val category = HashMap<String, Any>()
        category[ApiConstant.CID] = textAffirmationData?.category?.id ?: 0
        val subCategoryList = mutableListOf<HashMap<String, Any>>()
        textAffirmationData?.category?.subCategory?.forEach { subData ->
            val subCategory = HashMap<String, Any>()
            subCategory[ApiConstant.SCID] = subData.id ?: 0
            subCategory[ApiConstant.CID] = textAffirmationData?.category?.id ?: 0
            subCategoryList.add(subCategory)
        }
        category[ApiConstant.SUB_CATEGORY] = subCategoryList
        categoryList.add(category)
        return Gson().toJson(categoryList)
    }

    fun setCreateAffirmationDetails() {
        if (createAffirmationDetails?.isEdit == true) {
            if(isTranscript){
                transcript.set(createAffirmationDetails?.transcript)
                return
            }else{
                title.set(createAffirmationDetails?.affirmationTitle)
                createAffirmation.set(createAffirmationDetails?.affirmationDetail)
            }
        }
    }
}