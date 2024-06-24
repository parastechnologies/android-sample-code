package com.in2bliss.domain

import com.in2bliss.data.model.downloadResponse.DownloadStatus
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.utils.constants.AppConstant
import kotlinx.coroutines.flow.SharedFlow

    interface DownloadStatusListenerInterface {

    /**
     * Getting downloading status
     * */
    fun getDownloadStatus(): SharedFlow<DownloadStatus>

    /**
     * Updating downloading status
     * @param status
     * @param musicDetails
     * */
    fun setDownloadStatus(
        status: AppConstant.DownloadStatus,
        musicDetails: MusicDetails?
    )
}