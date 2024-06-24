package com.in2bliss.utils.constants

object AppConstant {

    const val IS_LOGIN = "IsLogin"
    const val DU = "DU"
    const val CATEGORY_LIST = "CategoryList"
    const val CATEGORY_TITLE = "CATEGORY_TITLE"
    const val IS_AFFIRMATION_CATEGORIES_FILTER = "AFFIRMATION_CATEGORIES_FILTER"
    const val IS_SEARCH_FILTER_RESULT = "IS_SEARCH_FILTER_RESULT"
    const val IS_SEARCH = "IS_SEARCH"
    const val TERMS_CONDITION = "TERMS_CONDITION"
    const val PRIVACY_POLICY = "PRIVACY_POLICY"
    const val ABOUT_US = "ABOUT_US"
    const val UNAUTHORISED = "UNAUTHORISED"
    const val CATEGORY_NAME = "CATEGORY_NAME"
    const val REAL_CATEGORY = "REAL_CATEGORY"
    const val STREAK_COUNT = "STREAK_COUNT"
    const val CHANGE_GRAVITY = "changeGravity"
    const val IS_GUIDED = "IS_GUIDED"
    const val IS_DOWNLOAD = "IS_DOWNLOAD"
    const val EDIT_BUNDLE = "EDIT_BUNDLE"
    const val EDIT_GRATITUDE_BUNDLE = "EDIT_GRATITUDE_BUNDLE"
    const val CATEGORY_TYPE = "CATEGORY_TYPE"
    const val DURATION = "DURATION"
    const val SMALL_ID = "id"
    const val CUSTOM_MIN = "CUSTOM_MIN"
    const val CUSTOM_HOURS = "CUSTOM_HOURS"
    const val PLAY_MUSIC_MEDIATION = "PLAY_MUSIC_MEDIATION"
    const val SILENT_MEDITATION = "SILENT_MEDITATION"
    const val MUSIC_MEDITATION = "MUSIC_MEDITATION"
    const val MEDITATION_TYPE = "MEDITATION_TYPE"
    const val TIMER_INTERVAL = 1000
    const val PLAY_ENDING_BELL = "PLAY_ENDING_BELL"
    const val DATE = "DATE"
    const val IS_GENERAL = "IS_GENERAL"
    const val CATEGORY_ID = "CATEGORY_ID"
    const val SCREEN_TITLE = "SCREEN_TITLE"
    const val SUB_CATEGORY_ID = "SUB_CATEGORY_ID"
    const val IS_MUSIC_CATEGORY = "IS_MUSIC_CATEGORY"
    const val GUIDED_AFFIRMATION = "GUIDED_AFFIRMATION"
    const val TYPE = "TYPE"
    const val NOTIFICATION_TYPE = "NOTIFICATION_TYPE"
    const val TRANSCRIPT = "TRANSCRIPT"
    const val PLAYER = "PLAYER"
    const val DATA_TYPE = "DATA_TYPE"
    const val SCREEN_TYPE = "SCREEN_TYPE"
    const val DOWNLOAD_STATUS = ""
    const val CREATE_AFFIRMATION = "CREATE_AFFIRMATION"

    // Used to get the id from the Step One Activity, When select reason
    const val REASON_ID = "RID"
    const val RECORDING = "RECORDING"
    const val ANDROID = "android"
    const val GMAIL = "gmail"
    const val START_TIME = "START_TIME"
    const val END_TIME = "END_TIME"
    const val DESCRIPTION = "DESCRIPTION"
    const val IMAGE_STRING = "IMAGE_STRING"
    const val ADD_AFFIRMATION_TYPE = "ADD_AFFIRMATION_TYPE"
    const val AFFIRMATION = "AFFIRMATION"
    const val MY_AFFIRMATION = "MY_AFFIRMATION"
    const val FAVOURITE = "FAVOURITE"
    const val MUSIC_DETAILS = "MUSIC_DETAILS"
    const val EDIT = "EDIT"
    const val NOTIFICATION_ADD = "NOTIFICATION_ADD"
    const val JOURNAL_DATA = "JOURNAL_DATA"
    const val TEXT_AFFIRMATION_DATA = "TEXT_AFFIRMATION_DATA"
    const val ID = "ID"
    const val MUSIC_CATEGORIES_DATA = "MUSIC_CATEGORIES_DATA"
    const val MUSIC_LIST_DATA = "MUSIC_CATEGORIES_DATA"
    const val MUSIC_CUSTOMIZE_DETAIL = "MUSIC_CUSTOMIZE_DETAIL"
    const val CUSTOMIZE = "CUSTOMIZE"
    const val AUDIO = "AUDIO"
    const val SUBSCRIPTION = "SUBSCRIPTION"

    enum class HomeCategory {
        GUIDED_AFFIRMATION,
        GUIDED_SLEEP,
        GUIDED_MEDITATION,
        QUOTES,
        MUSIC,
        CREATE_AFFIRMATION,
        WISDOM_INSPIRATION,
        MEDITATION_TRACKER,
        JOURNAL,
        DOWNLOAD,
        TEXT_AFFIRMATION,
        SLEEP,
        SLEEP_AFFIRMATION,
        SLEEP_MEDIATION,
        STEP_FIVE
    }

    enum class DownloadStatus() {
        DOWNLOADING,
        DOWNLOAD_COMPLETE,
        NOT_DOWNLOAD
    }

    enum class SeeAllType{
        EXPLORE,
        SLEEP
    }

    enum class CreatedAffirmationEdit{
        CREATE_AFFIRMATION,
        MY_AFFIRMATION,
        SEE_ALL_AFFIRMATION
    }

    enum class SubscriptionStatus{
        SIGN_UP_FLOW,
        SUBSCRIPTION_EXPIRED
    }
}