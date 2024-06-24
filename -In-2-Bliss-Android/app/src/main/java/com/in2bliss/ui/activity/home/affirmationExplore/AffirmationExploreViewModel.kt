package com.in2bliss.ui.activity.home.affirmationExplore

import androidx.databinding.ObservableField
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.home.commonAdapter.MusicDetailAdapter
import com.in2bliss.ui.activity.home.commonAdapter.MusicListAdapter
import com.in2bliss.ui.activity.home.commonAdapter.RecentSearchAdapter
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getFavouriteHashMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AffirmationExploreViewModel @Inject constructor(
    private val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface,
    private val imageRequest: ImageRequest.Builder,
    private val imageLoader: ImageLoader
) : BaseViewModel() {

    val streak = ObservableField("00")
    val search = ObservableField("")
    var isGeneral = ObservableField("")
    var categoryType: AppConstant.HomeCategory? = null
    var categoryId: String? = null
    var isGuided: Boolean? = false
    var subCategoryId: String? = null
    var rowPosition: Int? = null
    var columnPosition: Int? = null
    var favMusicId: Int? = null
    var filterStart: String? = null
    var type: String? = null
    var filterEnd: String? = null
    var isFavourite: Boolean? = null
    var nestedAdapter: MusicDetailAdapter? = null

    /** Using type when navigate from the guided sleep and need to check type like affirmation , meditation etc*/
    var typeForGuidedSleep: Int? = null

    private val mutableMusicListResponse by lazy {
        MutableSharedFlow<Resource<MusicList>>()
    }
    val musicListResponse by lazy { mutableMusicListResponse.asSharedFlow() }

    private val mutableFavouriteAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val favouriteAffirmation by lazy {
        mutableFavouriteAffirmation.asSharedFlow()
    }

    val musicListAdapter by lazy {
        MusicListAdapter(
            isSeeAll = true,
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest,
            categoryType = categoryType
        )
    }

    val recentSearchAdapter by lazy {
        RecentSearchAdapter()
    }

    /**
     * Home guided affirmation api request
     * */
    private fun homeGuidedAffirmation() {
        networkCallIo {

            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.CID] = categoryId.toString()
            hashMap[ApiConstant.SCID] = subCategoryId.toString()
            hashMap[ApiConstant.SEARCH] = search.get().orEmpty()
            hashMap[ApiConstant.GENERAL_STATUS] = isGeneral.get().orEmpty()

            mutableMusicListResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.homeGuidedAffirmation(hashMap)
                },
                apiName = ApiConstant.GUIDED_AFFIRMATION
            )
            mutableMusicListResponse.emit(
                value = response
            )
        }
    }

    /**
     * Home guided affirmation api request
     * */
    private fun homeGuidedWisdom() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.CID] = categoryId.toString()
            hashMap[ApiConstant.SCID] = subCategoryId.toString()
            hashMap[ApiConstant.SEARCH] = search.get().orEmpty()
            hashMap[ApiConstant.GENERAL_STATUS] = "1"

            mutableMusicListResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.homeGuidedWisdom(hashMap)
                },
                apiName = ApiConstant.GUIDED_AFFIRMATION
            )
            mutableMusicListResponse.emit(
                value = response
            )
        }
    }

    /**
     * Home my affirmation api request
     * */
    private fun homeMyAffirmation() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.SEARCH] = search.get().orEmpty()

            mutableMusicListResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.myAffirmation(hashMap)
                },
                apiName = ApiConstant.GUIDED_AFFIRMATION
            )
            mutableMusicListResponse.emit(
                value = response
            )
        }
    }

    /**
     * home Guided meditation api request
     * */
    private fun homeGuidedMeditation() {
        networkCallIo {

            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.CID] = categoryId.toString()
            hashMap[ApiConstant.SCID] = subCategoryId.toString()
            hashMap[ApiConstant.SEARCH] = search.get().orEmpty()
            hashMap[ApiConstant.GENERAL_STATUS] = isGeneral.get().orEmpty()
            if (filterStart != null && filterEnd != null) {
                hashMap[ApiConstant.START_TIME] = filterStart.orEmpty()
                hashMap[ApiConstant.END_TIME] = filterEnd.orEmpty()
            }

            mutableMusicListResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.homeGuidedMeditation(hashMap)
                },
                apiName = ApiConstant.GUIDED_MEDIATION
            )
            mutableMusicListResponse.emit(
                value = response
            )
        }
    }

    /**
     * Guided sleep
     * */
    private fun guidedSleep() {
        networkCallIo {

            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.SEARCH] = search.get().orEmpty()

            mutableMusicListResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.homeGuidedSleep(hashMap)
                },
                apiName = ApiConstant.GUIDED_MEDIATION
            )
            mutableMusicListResponse.emit(
                value = response
            )
        }
    }

    /**
     * Mark meditation as favourite
     * */
    private fun favouriteAffirmation() {
        networkCallIo {

            val hashMap = getFavouriteHashMap(
                category = categoryType(
                    categoryName = categoryType,
                    type = typeForGuidedSleep
                ),
                isFavourite = isFavourite,
                favMusicId = favMusicId,
                isSleep = categoryType == AppConstant.HomeCategory.GUIDED_SLEEP
            )

            mutableFavouriteAffirmation.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.favouriteAffirmation(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
            mutableFavouriteAffirmation.emit(value = response)
        }
    }

    fun apiRequest() {
        val apiName = when (categoryType) {
            AppConstant.HomeCategory.GUIDED_MEDITATION -> ApiConstant.GUIDED_MEDIATION
            AppConstant.HomeCategory.GUIDED_AFFIRMATION -> ApiConstant.GUIDED_AFFIRMATION
            AppConstant.HomeCategory.WISDOM_INSPIRATION -> ApiConstant.GUIDED_WISDOM
            AppConstant.HomeCategory.CREATE_AFFIRMATION -> ApiConstant.MY_AFFIRMATION
            AppConstant.HomeCategory.GUIDED_SLEEP -> ApiConstant.SLEEP
            else -> ""
        }
        retryApiRequest(apiName = apiName)
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
            ApiConstant.GUIDED_MEDIATION -> homeGuidedMeditation()
            ApiConstant.GUIDED_AFFIRMATION -> homeGuidedAffirmation()
            ApiConstant.GUIDED_WISDOM -> homeGuidedWisdom()
            ApiConstant.MY_AFFIRMATION -> homeMyAffirmation()
            ApiConstant.SLEEP -> guidedSleep()
        }
    }

    fun favouriteNotify(fav: Int) {
        rowPosition?.let { row ->
            columnPosition?.let { column ->
                val currentList = musicListAdapter.currentList
                currentList[row].data?.toMutableList()
                    ?.get(column)?.favouriteStatus = fav
                musicListAdapter.submitList(currentList.toMutableList())
                nestedAdapter?.notifyItemChanged(column)
            }
        }
    }
}