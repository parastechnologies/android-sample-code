package com.in2bliss.data.model.recordings

import android.net.Uri

data class RecordingDataModel(
    val mediaUri: Uri,
    val title: String = "Title",
    val date: String = "Date",
    val time: String = "Time",
    var isPlaying : Boolean = false,
    var startProgress :String = "00:00",
    var endProgress :String = "00:00",
    var progress : Int = 0,
    var maxProgress : Int = 0
)
