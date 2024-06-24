package com.in2bliss.ui.activity.home.fragment.explore

import android.util.Log
import androidx.databinding.ObservableField
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.R
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.explore.ExploreSelect
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.home.commonAdapter.MusicDetailAdapter
import com.in2bliss.ui.activity.home.commonAdapter.MusicListAdapter
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getFavouriteHashMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface,
    private val imageRequest: ImageRequest.Builder,
    private val imageLoader: ImageLoader
) : BaseViewModel() {

    val search = ObservableField("")
    var rowPosition: Int? = null
    var columnPosition: Int? = null
    var favMusicId: Int? = null
    var categoryType: AppConstant.HomeCategory? = AppConstant.HomeCategory.GUIDED_AFFIRMATION
    var nestedAdapter: MusicDetailAdapter? = null
    var isFavourite: Boolean? = null

    private val mutableExploreListResponse by lazy {
        MutableSharedFlow<Resource<MusicList>>()
    }
    val exploreListResponse by lazy { mutableExploreListResponse.asSharedFlow() }

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
            imageRequest = imageRequest
        )
    }

    val exploreListAdapter by lazy {
        ExploreItemAdapter()
    }


    /**
     * Home guided affirmation api request
     * */
    private fun getExplore() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.TYPE] = when (categoryType) {
                AppConstant.HomeCategory.GUIDED_MEDITATION -> ApiConstant.ExploreType.MEDITATION.value
                AppConstant.HomeCategory.WISDOM_INSPIRATION -> ApiConstant.ExploreType.WISDOM.value
                AppConstant.HomeCategory.SLEEP_AFFIRMATION -> "2"
                AppConstant.HomeCategory.SLEEP_MEDIATION -> "3"
                else -> ApiConstant.ExploreType.AFFIRMATION.value
            }
            Log.d("sacsacsacjk", "getExplore: ${categoryType?.name}")
            hashMap[ApiConstant.SEARCH] = search.get().orEmpty()
            mutableExploreListResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getExplore(hashMap)
                }, apiName = ApiConstant.EXPLORE
            )
            mutableExploreListResponse.emit(
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
                category = categoryType,
                isFavourite = isFavourite,
                favMusicId = favMusicId
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

    fun getAffirmationTypeList(): ArrayList<ExploreSelect> {
        return arrayListOf(
            ExploreSelect(
                title = R.string.guided_affirmation,
                affirmationType = AppConstant.HomeCategory.GUIDED_AFFIRMATION
            ), ExploreSelect(
                title = R.string.guided_meditation,
                affirmationType = AppConstant.HomeCategory.GUIDED_MEDITATION
            ), ExploreSelect(
                title = R.string.sleep_affirmation, affirmationType = AppConstant.HomeCategory.SLEEP_AFFIRMATION
            ), ExploreSelect(
                title = R.string.sleep_mediation,
                affirmationType = AppConstant.HomeCategory.SLEEP_MEDIATION
            ),ExploreSelect(
                title = R.string.wisdom_inspiration,
                affirmationType = AppConstant.HomeCategory.WISDOM_INSPIRATION
            )
        )
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.EXPLORE -> getExplore()
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
        }
    }

    fun favouriteNotify(fav: Int) {
        rowPosition?.let { row ->
            columnPosition?.let { column ->
                val currentList = musicListAdapter.currentList
                currentList[row].data?.toMutableList()?.get(column)?.favouriteStatus = fav
                musicListAdapter.submitList(currentList.toMutableList())
                nestedAdapter?.notifyItemChanged(column)
            }
        }
    }
}