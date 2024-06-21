package com.highenergymind.data


/**
 * Created by Puneet on 15/05/24
 */
data class CustomAffirmationModel(
    var isSilent: Boolean = false,
    var speakerIndex: Int = 0,
    var delay: Int = 5, /* seconds */
    var affirmationLength: Int = 0,
    var musicLength: Int = 0,
    var speakerAudio: List<AudioFile>,
    var isSpeakerChange: Boolean = false,
    var isCustomizedLength: Boolean = false,
    var isRepeatUi: Boolean = false
)
