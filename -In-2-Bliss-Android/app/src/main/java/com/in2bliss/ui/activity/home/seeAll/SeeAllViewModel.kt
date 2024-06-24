package com.in2bliss.ui.activity.home.seeAll

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.home.seeAll.pagingSource.ExploreAndSleepSeeAllPagingSource
import com.in2bliss.ui.activity.home.seeAll.pagingSource.SeeAllPagingSource
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getFavouriteHashMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class SeeAllViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder
) : BaseViewModel() {

    var categoryName: AppConstant.HomeCategory? = null
    var type: Int? = null
    var categoryType: String? = null
    var isGeneral: String? = null
    var seeAllType: AppConstant.SeeAllType? = null
    var categoryId: String? = null
    var musicFavourite = false
    var isFavourite = false
    var favMusicId: Int? = null
    var subCategoryId: String? = null
    var position: Int? = null
    private var job: Job? = null

    val adapter by lazy {
        SeeAllAdapter(
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest,
            categoryType = categoryName
        )
    }

    private val mutableFavouriteAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val favouriteAffirmation by lazy {
        mutableFavouriteAffirmation.asSharedFlow()
    }

    private fun getExploreAll(): Flow<PagingData<SeeAllResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
            ), pagingSourceFactory = {
                ExploreAndSleepSeeAllPagingSource(
                    type = categoryType,
                    dataType = type.toString(),
                    apiType = seeAllType,
                    apiHelperInterface = apiHelperInterface
                )

            }, initialKey = 0
        ).flow
    }

    fun seeAllList(): Flow<PagingData<SeeAllResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                SeeAllPagingSource(
                    type = type,
                    categoryId = categoryId,
                    subCategoryId = subCategoryId,
                    apiHelperInterface = apiHelperInterface,
                    category = categoryName,
                    isGeneral = isGeneral
                )
            }, initialKey = 0
        ).flow
    }

    /**
     * Mark meditation as favourite
     * */
    private fun favouriteAffirmation() {
        networkCallIo {

            val hashMap = getFavouriteHashMap(
                category = categoryType(
                    categoryName = categoryName,
                    type = type
                ),
                isFavourite = musicFavourite,
                favMusicId = favMusicId
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

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
        }
    }

    fun favouriteNotify(fav: Int) {
        position?.let { row ->
            viewModelScope.launch {
                val currentList = adapter.snapshot().items.toMutableList()
                currentList[row].favouriteStatus = fav
                adapter.submitData(PagingData.from(currentList.toImmutableList()))
                adapter.notifyItemChanged(row)
            }
        }
    }

    fun gettingMusicList() {
        if (job != null) job?.cancel()
        job = viewModelScope.launch {
            ensureActive()
            adapter.type = type

            when (seeAllType) {

                AppConstant.SeeAllType.EXPLORE, AppConstant.SeeAllType.SLEEP -> {
                    getExploreAll().collectLatest { exploreAllSeeList ->
                        viewModelScope.launch {
                            adapter.submitData(exploreAllSeeList)
                        }
                    }
                }

                else -> {
                    seeAllList().collectLatest { seeAllList ->
                        viewModelScope.launch {
                            adapter.submitData(seeAllList)
                        }
                    }
                }
            }
        }
    }
}