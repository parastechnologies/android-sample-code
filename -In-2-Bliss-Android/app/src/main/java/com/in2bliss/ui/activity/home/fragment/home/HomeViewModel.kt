package com.in2bliss.ui.activity.home.fragment.home

import androidx.databinding.ObservableField
import com.bumptech.glide.RequestManager
import com.in2bliss.R
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.GettingStarted
import com.in2bliss.data.model.HomeData
import com.in2bliss.data.model.HomeResponse
import com.in2bliss.data.model.ShareResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface,
//    private val imageRequest: ImageRequest.Builder,
//    private val imageLoader: ImageLoader
) : BaseViewModel() {

    val categoryName = ObservableField("Self-love")
    val date = ObservableField("12 May")
    val affirmation = ObservableField("")


    private val mutableShareUrl by lazy {
        MutableSharedFlow<Resource<ShareResponse>>()
    }

    val shareUrl by lazy {
        mutableShareUrl.asSharedFlow()
    }

    var shareType = ""
    var shareId = ""


    var type: String? = null
    private val mutableHomeData by lazy {
        MutableSharedFlow<Resource<HomeResponse>>()
    }
    val homeData by lazy { mutableHomeData.asSharedFlow() }


    val gettingStartedAdapter by lazy {
        HomeGettingStartedAdapter()
    }

    val musicListAdapter by lazy {
        MusicAdapter(
            isSeeAll = false,
            requestManager = requestManager,
        )
    }

    val gettingStartedList = arrayListOf(
        GettingStarted(R.drawable.ic_sound, "Guided \nAffirmations"),
        GettingStarted(R.drawable.ic_moon, "Guided \nSleep"),
        GettingStarted(R.drawable.ic_meditation, "Guided \nMeditation"),
        GettingStarted(R.drawable.ic_quote, "Quotes"),
        GettingStarted(R.drawable.ic_music, "Music"),
        GettingStarted(R.drawable.ic_affirmation, "Create \nAffirmations"),
        GettingStarted(R.drawable.ic_wisdom, "Wisdom/Inspiration"),
        GettingStarted(R.drawable.ic_yoga, "Meditation \nTracker"),
        GettingStarted(R.drawable.ic_journal, "Gratitude \nJournal"),
    )

    private fun getHomeData() {
        networkCallIo {
            mutableHomeData.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getHomeData()
                },
                apiName = ApiConstant.Home
            )
            mutableHomeData.emit(
                value = response
            )
        }
    }


    val musicList = arrayListOf(
        HomeData(
            "Recently played",
            arrayListOf(
                HomeData.MusicDescription(
                    "I am deserving of happiness and fulfillment.",
                    "This guided affirmation is designed to help you connect with your...",
                    R.drawable.ic_temp_music_1,
                    false
                ),
                HomeData.MusicDescription(
                    "I am grateful for all the abundance in my life.",
                    "This guided affirmation is designed to help you connect with your...",
                    R.drawable.ic_temp_music_2,
                    true
                )
            )
        ),
        HomeData(
            "Curated to you",
            arrayListOf(
                HomeData.MusicDescription(
                    "I am deserving of happiness and fulfillment.",
                    "This guided affirmation is designed to help you connect with your...",
                    R.drawable.ic_temp_music_1,
                    false
                ),
                HomeData.MusicDescription(
                    "I am grateful for all the abundance in my life.",
                    "This guided affirmation is designed to help you connect with your...",
                    R.drawable.ic_temp_music_2,
                    true
                )
            )
        ),
        HomeData(
            "Recently added",
            arrayListOf(
                HomeData.MusicDescription(
                    "I am deserving of happiness and fulfillment.",
                    "This guided affirmation is designed to help you connect with your...",
                    R.drawable.ic_temp_music_1,
                    true
                ),
                HomeData.MusicDescription(
                    "I am grateful for all the abundance in my life.",
                    "This guided affirmation is designed to help you connect with your...",
                    R.drawable.ic_temp_music_2,
                    false
                )
            )
        )
    )

    fun getCategoryType(
    ): AppConstant.HomeCategory {
        return when (type) {
            "0" -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
            "1" -> AppConstant.HomeCategory.GUIDED_MEDITATION
            "2" -> AppConstant.HomeCategory.MUSIC
            "4" -> AppConstant.HomeCategory.WISDOM_INSPIRATION
            else -> AppConstant.HomeCategory.TEXT_AFFIRMATION
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
            ApiConstant.Home -> getHomeData()
            ApiConstant.SHARE_URL -> shareUrl()
        }
    }
}