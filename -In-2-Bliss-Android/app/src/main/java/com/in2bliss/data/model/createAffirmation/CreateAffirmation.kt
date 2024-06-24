package com.in2bliss.data.model.createAffirmation

import com.in2bliss.data.networkRequest.ApiConstant

data class CreateAffirmation(
    var affirmationTitle: String? = null,
    var affirmationDetail: String? = null,
    var transcript: String? = null,
    var audioFileStringUri: String? = null,
    var affirmationBackground: String? = null,
    var audioType: ApiConstant.AffirmationAudioType? = null,
    var isGalleryImage: Boolean? = false,
    var audioDuration: Long? = null,
    val isEdit: Boolean = false,
    val affirmationId: Int? = null,
    val screenType: String? = null,
    val type: Int? = null,
    val screenName: String? = null,
    var convertedFileName: String? = null,
    var backgroundMusic: String? = null,
    var audioName: String? = null
)
