package com.in2bliss.data.model.audioConversion

import android.net.Uri

data class AudioConversion(
    val isConversionComplete: Boolean,
    val convertedFileUri: Uri?,
    val convertedFilePath: String?,
    val isFailed: Boolean
)
