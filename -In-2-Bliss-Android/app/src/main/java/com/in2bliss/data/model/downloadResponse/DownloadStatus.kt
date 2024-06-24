package com.in2bliss.data.model.downloadResponse

import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.utils.constants.AppConstant

data class DownloadStatus(
    val downloadStatus : AppConstant.DownloadStatus,
    val musicDetails : MusicDetails?
)
