package com.in2bliss.ui.activity.home.quote

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.QuotesResponseModel
import com.in2bliss.domain.ApiHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Create by Paras on 10/31/2023
 */
@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    var quotesId: String? = null
    val viewPagerAdapter by lazy {
        QuotesAdapter(
            requestManager = requestManager
        )
    }

    fun getQuotesList(): LiveData<PagingData<QuotesResponseModel.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                QuotesPagingSource(apiHelperInterface = apiHelperInterface, quotesId)
            }, initialKey = 0
        ).liveData
    }

    override fun retryApiRequest(apiName: String) {}

}