package com.in2bliss.ui.activity.home.fragment.journal

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.journalList.GuidedJournalListResponse
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
class JournalViewModel @Inject constructor(private val apiHelperInterface: ApiHelperInterface) :
    BaseViewModel() {

    val streak = ObservableField("0")
    val search = ObservableField("")
    var deleteJournalId: Int? = null
    var position: Int? = null
    var categoryType: AppConstant.HomeCategory? = null

    val adapter by lazy {
        JournalAdapter()
    }

    private val mutableDeleteJournal by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val deleteJournal by lazy { mutableDeleteJournal.asSharedFlow() }

    private fun deleteJournal() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.ID] = deleteJournalId.toString()

            mutableDeleteJournal.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.deleteJournal(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.DELETE_JOURNAL
            )
            mutableDeleteJournal.emit(value = response)
        }
    }

    fun getGuidedJournal(): LiveData<PagingData<GuidedJournalListResponse.Data>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GuidedJournalPagingSource(
                    apiHelperInterface = apiHelperInterface,
                    search = search.get().orEmpty()
                )
            },
            initialKey = 0
        ).liveData
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.DELETE_JOURNAL -> deleteJournal()
        }
    }
}