package com.in2bliss.ui.activity.home.profileManagement.download

import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadVM @Inject constructor(
    private val requestManager: RequestManager
) : BaseViewModel() {

    val downloadAdapter by lazy {
        DownloadAdapter(
            requestManager = requestManager
        )
    }

    override fun retryApiRequest(apiName: String) {
    }
}