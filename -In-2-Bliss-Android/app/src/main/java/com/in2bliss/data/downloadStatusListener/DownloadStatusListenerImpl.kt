package com.in2bliss.data.downloadStatusListener

import com.in2bliss.data.model.downloadResponse.DownloadStatus
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.utils.constants.AppConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DownloadStatusListenerImpl : DownloadStatusListenerInterface {

    private var job: Job? = null

    private val mutableDownloadStatus by lazy {
        MutableSharedFlow<DownloadStatus>()
    }

    /**
     * Getting downloading status
     * */
    override fun getDownloadStatus(): SharedFlow<DownloadStatus> {
        return mutableDownloadStatus.asSharedFlow()
    }

    /**
     * Updating downloading status
     * @param status
     * @param musicDetails
     * */
    override fun setDownloadStatus(
        status: AppConstant.DownloadStatus,
        musicDetails: MusicDetails?
    ) {
        if (job != null) job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val downloadStatus = DownloadStatus(
                downloadStatus = status,
                musicDetails = musicDetails
            )
            mutableDownloadStatus.emit(value = downloadStatus)
        }
    }
}