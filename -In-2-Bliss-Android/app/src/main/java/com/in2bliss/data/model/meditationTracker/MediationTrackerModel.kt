package com.in2bliss.data.model.meditationTracker
data class MediationTrackerModel(
    var hours: Int? = 0,
    var min: Int? = 0,
    var playEndBell: Boolean? = false,
    var musicId: Int? = null,
    var musicUrl: String? = null,
    var musicTitle : String? = null,
    var musicImage : String? = null
)