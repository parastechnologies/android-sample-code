package com.in2bliss.ui.activity.home.affirmation.affirmationList

import androidx.databinding.ObservableField
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.AdminAffirmationResponse
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.data.model.ShareResponse
import com.in2bliss.data.model.TextAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.home.affirmation.TextAffirmationListAdapter
import com.in2bliss.ui.activity.home.affirmation.affirmationList.practiceAffirmation.PracticeAllAffirmationAdapter
import com.in2bliss.ui.activity.home.affirmation.textList.TextListAdapter
import com.in2bliss.ui.activity.home.affirmation.textList.TextPagingSource
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AffirmationListViewModel @Inject constructor(
    private val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface,
    val imageRequest: ImageRequest.Builder,
    val imageLoader: ImageLoader
) : BaseViewModel() {

    val search = ObservableField("")
    var isSearch = false
    var position = -1
    var isFavourite: Boolean = true
    var affirmationID: Int? = null
    var categoryType: AppConstant.HomeCategory? = null
    var notificationAdminId = ""


    var shareType = ""
    var shareId = ""


    private val mutableAffirmationDelete by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }

    val affirmationDelete by lazy { mutableAffirmationDelete.asSharedFlow() }


    private val mutableAdminAffirmation by lazy {
        MutableSharedFlow<Resource<AdminAffirmationResponse>>()
    }
    val adminAffirmation by lazy { mutableAdminAffirmation.asSharedFlow() }

    private val mutableFavouriteAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val favouriteAffirmation by lazy {
        mutableFavouriteAffirmation.asSharedFlow()
    }

    private val mutableShareUrl by lazy {
        MutableSharedFlow<Resource<ShareResponse>>()
    }

    val shareUrl by lazy {
        mutableShareUrl.asSharedFlow()
    }

    val adapter by lazy {
        AffirmationListAdapter(
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest
        )
    }

    val viewPagerAdapter by lazy {
        PracticeAllAffirmationAdapter(
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest
        )
    }

    val viewPagerAdapterText by lazy {
        TextAffirmationListAdapter(
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest
        )
    }

    val adapterText by lazy {
        TextListAdapter(
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest
        )
    }

    /**
     * Mark affirmation as favourite
     * */
    private fun favouriteAffirmation() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Affirmation.value.toString()
            hashMap[ApiConstant.STATUS] = if (isFavourite) {
                ApiConstant.FavouriteStatus.RemoveFavourite.value.toString()
            } else ApiConstant.FavouriteStatus.Favourite.value.toString()
            hashMap[ApiConstant.AFFIRMATION_ID] = affirmationID.toString()
            hashMap[ApiConstant.TYPE] = ApiConstant.AffirmationFavouriteType.Text.value.toString()
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

    /**
     * meditation/session/history api request
     * */
    private fun adminAffirmation() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.AFFIRMATION_ID] = notificationAdminId


            mutableAdminAffirmation.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.adminAffirmation(hashMap)
                },
                apiName = ApiConstant.ADMIN_AFFIRMATION
            )
            mutableAdminAffirmation.emit(
                value = response
            )
        }
    }


    /**
     * Delete affirmation api request
     * */
    private fun deleteAffirmationId() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.AFFIRMATION_ID] = affirmationID.toString()
            mutableAffirmationDelete.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.deleteAffirmation(
                        body = hashMap
                    )
                }, apiName = ApiConstant.DELETE_AFFIRMATION
            )
            mutableAffirmationDelete.emit(
                value = response
            )
        }
    }

    var affirmationId: String? = null

    /**
     * Getting text affirmation paging data
     * */
    fun getTextAffirmationList(): Flow<PagingData<AffirmationListResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TextAffirmationPagingSource(
                    apiHelperInterface = apiHelperInterface,
                    search = search.get()?.trim(),
                    affirmationId
                )
            },
            initialKey = 0
        ).flow
    }

    var id: String? = ""
    var favouriteType: String = "0"


    /**
     * Getting text affirmation paging data
     * */
    fun getAdminAffirmationList(onItemEmpty: (() -> Unit)?=null): Flow<PagingData<TextAffirmation>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TextPagingSource(
                    apiHelperInterface = apiHelperInterface,
                    id,
                    onItemEmpty
                )
            },
            initialKey = 0
        ).flow
    }


    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.DELETE_AFFIRMATION -> deleteAffirmationId()
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
            ApiConstant.ADMIN_AFFIRMATION -> adminAffirmation()
            ApiConstant.SHARE_URL -> shareUrl()
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

}

