package com.in2bliss.ui.activity.home.seeAll.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant

/**
 * Create by Paras on 10/04/2023
 */
class SeeAllPagingSource(
    private val type: Int? = null,
    private val apiHelperInterface: ApiHelperInterface,
    private val categoryId: String? = null,
    private val subCategoryId: String? = null,
    private val category: AppConstant.HomeCategory? = null,
    private val isGeneral:String?=null
) : PagingSource<Int, SeeAllResponse.Data>() {
    override fun getRefreshKey(state: PagingState<Int, SeeAllResponse.Data>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SeeAllResponse.Data> {
        return try {

            val position = params.key ?: 0
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.PAGE_NUMBER] = position.toString()
            hashMap[ApiConstant.TYPE] = type.toString()

            if (category == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                category == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                category == AppConstant.HomeCategory.WISDOM_INSPIRATION
            ) {
                hashMap[ApiConstant.CID] = categoryId.toString()
                hashMap[ApiConstant.SCID] = subCategoryId.toString()
                hashMap[ApiConstant.GENERAL_STATUS] = isGeneral.toString()
            }

            val response = when (category) {
                AppConstant.HomeCategory.GUIDED_MEDITATION -> {
                    apiHelperInterface.mediationSeeALl(
                        body = hashMap
                    )
                }

                AppConstant.HomeCategory.GUIDED_AFFIRMATION -> {
                    apiHelperInterface.guidedAffirmationSeeALl(
                        body = hashMap
                    )
                }

                AppConstant.HomeCategory.CREATE_AFFIRMATION -> {
                    apiHelperInterface.myAffirmationSeeAll(
                        body = hashMap
                    )
                }

                AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
                    apiHelperInterface.wisdomSeeAll(
                        body = hashMap
                    )
                }

                else -> {
                    apiHelperInterface.mediationSeeALl(
                        body = hashMap
                    )
                }
            }

            val nextKey = if ((response.body()?.data?.size
                    ?: 9) % 10 != 0 || response.body()?.data?.size == 0
            ) {
                null
            } else position + 1

            LoadResult.Page(
                data = response.body()?.data ?: arrayListOf(),
                prevKey = null,
                nextKey = nextKey
            )

        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}