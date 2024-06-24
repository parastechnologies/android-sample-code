package com.in2bliss.data.model.musicDetails

import com.in2bliss.utils.constants.AppConstant

data class MusicDetails(
    val musicTitle: String? = null,
    val backgroundMusicTitle: String? = null,
    val musicDescription: String? = null,
    var musicUrl: String? = null,
    var backgroundMusicUrl: String? = null,
    val affirmationIntroduction: String? = null,
    val musicViews: Int? = null,
    var musicFavouriteStatus: Int? = null,
    val musicThumbnail: String? = null,
    val musicBackground: String? = null,
    val musicId: Int? = null,
    var duration: Double? = null,
    var musicCustomizeDetail: MusicCustomizeDetails? = null,
    var isCustomizationEnabled: Boolean = false,
    var ableToChangeBackgroundMusic: Boolean = false,
    val isSleep: Boolean = false,
    var downloadCategoryName:AppConstant.HomeCategory?=null,
)
