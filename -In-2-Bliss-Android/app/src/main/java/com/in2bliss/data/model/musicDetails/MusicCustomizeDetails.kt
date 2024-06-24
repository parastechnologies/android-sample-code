package com.in2bliss.data.model.musicDetails


data class MusicCustomizeDetails(
    var affirmationHour: Int? = null,
    var affirmationMinute: Int? = null,
    /** #affirmationSeconds variable is going to be use on Sleep_mediation only*/
    var affirmationSeconds: Int? = null,
    var backgroundMusicId: Int? = null,
    var musicCategoryId: Int? = null,
    var backgroundMusicTitle: String? = null,
    var backgroundMusicImage: String? = null,
    var backgroundMusicHour: Int? = null,
    var backgroundMusicMinute: Int? = null,
    val affirmationDuration: Int? = null,
    var isBackgroundMusicEnabled: Boolean = true,
    var isSaveForNextTime: Boolean = false,
    var backgroundMusicUrl: String? = null,
    var darkMode: Boolean = false,
    var isSleep: Boolean = false,
    var defaultMusicUrl:Boolean=false,
)
