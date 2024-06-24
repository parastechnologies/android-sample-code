package com.in2bliss.ui.activity.home.profileManagement.favourites

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bumptech.glide.RequestManager
import com.in2bliss.R
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.FavoritesType
import com.in2bliss.data.model.FavouritesResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getFavouriteHashMap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class FavouritesVM @Inject constructor(
    @ApplicationContext context: Context,
    private val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface,
) : BaseViewModel() {

    var favouriteType: String = "0"
    var categoryType: ApiConstant.ExploreType? = ApiConstant.ExploreType.AFFIRMATION
    var position: Int? = null
    var id: Int? = null
    var isFav: Int? = null

    val favouriteAdapter by lazy {
        FavouritesAdapter(
            requestManager = requestManager
        )
    }

    val favouritesTypeAdapter by lazy {
        FavouriteTypeAdapter()
    }

    private val mutableFavouriteAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val favouriteAffirmation by lazy {
        mutableFavouriteAffirmation.asSharedFlow()
    }

    fun getTypeList(context: Context): ArrayList<FavoritesType> {
        return arrayListOf(
            FavoritesType(
                title = context.getString(R.string.affirmation),
                array = affirmationList,
                ApiConstant.ExploreType.AFFIRMATION
            ),
            FavoritesType(
                title = context.getString(R.string.meditation), array = commonList,

                ApiConstant.ExploreType.MEDITATION
            ),
            FavoritesType(
                title = context.getString(R.string.music), array = commonList,
                ApiConstant.ExploreType.MUSIC
            ),
            FavoritesType(
                title = context.getString(R.string.wisdom), array = arrayListOf(),
                ApiConstant.ExploreType.WISDOM
            )
        )
    }

    private val affirmationList = arrayListOf(
        context.getString(R.string.text),
        context.getString(R.string.my_affirmation),
        context.getString(R.string.guided),
        context.getString(R.string.sleep)
    )
    private val commonList =
        arrayListOf(context.getString(R.string.normal), context.getString(R.string.sleep1))


    /**
     * Getting get favourites paging data
     * */
    fun favouritesList(): Flow<PagingData<FavouritesResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FavouritePagingSource(
                    apiHelperInterface = apiHelperInterface,
                    favouritesType = categoryType?.value ?: "0",
                    type = favouriteType
                )
            },
            initialKey = 0
        ).flow
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
        }
    }

    fun getCategoryType(): AppConstant.HomeCategory {
        return when (categoryType) {
            ApiConstant.ExploreType.AFFIRMATION -> {
                when (favouriteType) {
                    "0" -> AppConstant.HomeCategory.TEXT_AFFIRMATION
                    "1" -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                    "2" -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                    else -> AppConstant.HomeCategory.GUIDED_SLEEP
                }
            }
            ApiConstant.ExploreType.MEDITATION -> {
                if (favouriteType == "0") {
                    AppConstant.HomeCategory.GUIDED_MEDITATION
                } else AppConstant.HomeCategory.GUIDED_SLEEP
            }
            ApiConstant.ExploreType.MUSIC -> {
                if (favouriteType == "0") {
                    AppConstant.HomeCategory.MUSIC
                } else AppConstant.HomeCategory.GUIDED_SLEEP
            }
            else -> AppConstant.HomeCategory.WISDOM_INSPIRATION
        }
    }

    /**
     * Mark meditation as favourite
     * */
    private fun favouriteAffirmation() {
        networkCallIo {
            val categoryType = categoryType(
                categoryName = getCategoryType(),
                type = categoryType?.value?.toInt() ?: 0
            )

            val hashMap = getFavouriteHashMap(
                category = categoryType,
                isFavourite = isFav == 1,
                favMusicId = id,
                isSleep = getCategoryType() == AppConstant.HomeCategory.GUIDED_SLEEP
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
}