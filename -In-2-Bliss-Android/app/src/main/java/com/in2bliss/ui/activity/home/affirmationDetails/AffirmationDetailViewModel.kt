package com.in2bliss.ui.activity.home.affirmationDetails

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.ShareResponse
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.convertTimeToSeconds
import com.in2bliss.utils.extension.getFavouriteHashMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffirmationDetailViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val views = ObservableField("")
    val title = ObservableField("")
    val description = ObservableField("")
    var shareType = ""
    var shareId=""

    var categoryType: AppConstant.HomeCategory? = null
    var musicDetails: MusicDetails? = null
    var isCustomized: Boolean = false
    var isDownloadingStarted = false

    private val mutableFavouriteAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val favouriteAffirmation by lazy {
        mutableFavouriteAffirmation.asSharedFlow()
    }

    private val mutableCustomizeAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val customizeAffirmation by lazy {
        mutableCustomizeAffirmation.asSharedFlow()
    }

    private val mutableDownloadStatus by lazy {
        MutableStateFlow(AppConstant.DownloadStatus.NOT_DOWNLOAD)
    }
    val downloadStatus by lazy { mutableDownloadStatus.asStateFlow() }
    private val mutableShareUrl by lazy {
        MutableSharedFlow<Resource<ShareResponse>>()
    }
    val shareUrl by lazy {
        mutableShareUrl.asSharedFlow()
    }

    fun changeDownloadStatus(
        downloadStatus: AppConstant.DownloadStatus
    ) {
        viewModelScope.launch {
            mutableDownloadStatus.emit(value = downloadStatus)
        }
    }

    private fun customizeAffirmation() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.AID] = musicDetails?.musicId.toString()
            hashMap[ApiConstant.DURATION_STATUS] =
                if (musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == true) {
                    ApiConstant.IsBackgroundMusicEnabled.ENABLED.value
                } else ApiConstant.IsBackgroundMusicEnabled.DISABLED.value
            val duration = convertTimeToSeconds(
                musicDetails?.musicCustomizeDetail?.affirmationHour,
                musicDetails?.musicCustomizeDetail?.affirmationMinute
            )
            val backgroundDuration = convertTimeToSeconds(
                musicDetails?.musicCustomizeDetail?.backgroundMusicHour,
                musicDetails?.musicCustomizeDetail?.backgroundMusicMinute
            )

            hashMap[ApiConstant.DURATION] = duration.toString()
            hashMap[ApiConstant.BACKGROUND_DURATION] = backgroundDuration.toString()

            if (musicDetails?.musicCustomizeDetail?.musicCategoryId != null) {
                hashMap[ApiConstant.CID] =
                    musicDetails?.musicCustomizeDetail?.musicCategoryId.toString()
                hashMap[ApiConstant.MID] =
                    musicDetails?.musicCustomizeDetail?.backgroundMusicId.toString()
            }

            mutableCustomizeAffirmation.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.customizeAffirmation(
                        body = hashMap
                    )
                }, apiName = ApiConstant.CUSTOMIZE_AFFIRMATION
            )
            mutableCustomizeAffirmation.emit(value = response)
        }
    }

    /**
     * Mark meditation as favourite
     * */
    private fun favouriteAffirmation() {
        networkCallIo {

            val hashMap = getFavouriteHashMap(
                category = categoryType,
                isFavourite = musicDetails?.musicFavouriteStatus == 1,
                favMusicId = musicDetails?.musicId,
                isSleep = musicDetails?.isSleep ?: false
            )

            mutableFavouriteAffirmation.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.favouriteAffirmation(
                        body = hashMap
                    )
                }, apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
            mutableFavouriteAffirmation.emit(value = response)
        }
    }


    private fun shareUrl() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.ID] = shareId
            hashMap[ApiConstant.TYPE] = shareType
            mutableShareUrl.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.shareUrl(hashMap)
                },
                apiName = ApiConstant.SHARE_URL
            )
            mutableShareUrl.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
            ApiConstant.CUSTOMIZE_AFFIRMATION -> customizeAffirmation()
            ApiConstant.SHARE_URL -> shareUrl()
        }
    }
}