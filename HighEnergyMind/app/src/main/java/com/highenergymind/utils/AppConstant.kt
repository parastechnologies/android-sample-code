package com.highenergymind.utils


/**
 * Created by developer on 28/02/24
 */
object AppConstant {
    const val IS_FROM_SIGN_UP = "isFromSignUp"
    const val SCREEN_FROM = "screenFrom"
    const val ANDROID = "android"
    const val INTENT_IMAGE = "image/*"

    const val TYPE_AFFIRMATION = "affirmation"
    const val TYPE_TRACK = "track"
    const val TRACK_DATA = "trackData"
    const val MUSIC_DATA = "musicData"
    const val MONTHLY = "monthly"
    const val YEARLY = "yearly"
    const val SEVEN_DAYS = "7days"
    const val IS_SEEN = "isSeen"
    const val SHARE_AFFIRMATION_DEEP_LINK =
        "https://php.parastechnologies.in/highMindEnergy/api/v1/api/inviteLink/"
    const val DOWNLOAD_APP_URL = "https://php.parastechnologies.in/highMindEnergy/api/v1/api/downloadApp"
    const val SHARE_MUSIC_DEEP_LINK =
        "https://php.parastechnologies.in/highMindEnergy/api/v1/api/inviteMusicLink/"

    enum class GENDER(val value: String) {
        MALE("male"),
        FEMALE("female"),
        OTHERS("others"),
        NO_MALE_NOR_FEMALE("normalenorfemale")
    }

    enum class SOCIAL(val value: String) {
        GOOGLE("google"),
        APPLE("apple")
    }

    enum class LANGUAGE(val value: String) {
        ENGLISH("English"),
        GERMAN("German")
    }

    enum class SUBSCRIPTION(val value: String) {
        MONTHLY("hem_monthly"),
        YEARLY("hem_yearly")
    }

    enum class SEE_ALL(val value: String) {
        LAST_PLAYER("last"),
        RECOMMENDATION("recommend"),
        PLAYLIST_MONTH("playlist"),
        POPULAR("popular"),
    }

}