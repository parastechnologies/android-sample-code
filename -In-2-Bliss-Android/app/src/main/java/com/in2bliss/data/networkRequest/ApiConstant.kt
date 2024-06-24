package com.in2bliss.data.networkRequest

object ApiConstant {

    /**
     * Api's end points
     */

    const val SIGNUP_REASONS = "signup/reasons"
    const val SIGNUP = "signup"
    const val ADD_NAME = "name/add"
    const val PICTURE_ADD = "picture/add"
    const val ADMIN_AFFIRMATION = "admin/affirmation"
    const val CATEGORY_LIST = "category/list"
    const val SHARE_DATA = "share/data"
    const val Home = "home"
    const val LOGIN = "login"
    const val SOCIAL_LOGIN = "social/login"
    const val REMINDER_SET = "reminder/set"
    const val CATEGORY_ADD = "category/add"
    const val ADD_AFFIRMATION = "affirmation/add"
    const val AFFIRMATION_UPDATE = "affirmation/update"
    const val TEXT_AFFIRMATION_LIST = "text/affirmation/list"
    const val ADMIN_AFFIRMATION_LIST = "home/affirmation/List"
    const val GRATITUDE_LIST = "gratitude/journal/list"
    const val UPLOAD = "upload"
    const val FAVOURITE_LIST = "favourite/list"
    const val GUIDED_MEDIATION = "guided/meditation"
    const val DELETE_AFFIRMATION = "affirmation/delete"
    const val FAVOURITE_AFFIRMATION = "favourite"
    const val MEDITATION_ALL = "meditation/all"
    const val MY_AFFIRMATION_SEE_ALL = "created/affirmation/all"
    const val AFFIRMATION_ALL = "affirmation/all"
    const val WISDOM_ALL = "wisdom/all"
    const val LOGOUT = "logout"
    const val ACCOUNT_DELETE = "account/delete"
    const val JOURNAL_BACKGROUND_IMAGES = "journal/background"
    const val AFFIRMATION_BACKGROUND_IMAGES = "affirmation/background"
    const val JOURNAL_ADD = "gratitude/journal/add"
    const val DELETE_JOURNAL = "gratitude/journal/delete"
    const val JOURNAL_STREAK = "gratitude/journal/calendar"
    const val EDIT_JOURNAL = "gratitude/journal/update"
    const val GUIDED_AFFIRMATION = "guided/affirmation"
    const val PROFILE_DETAIL = "profile/details"
    const val CONTACT_US = "contactus"
    const val NOTIFICATION_STATUS = "notification/status"
    const val NOTIFICATION_STATUS_SET = "notification/status/set"
    const val PROFILE_UPDATE = "profile/update"
    const val REMINDER_UPDATE = "reminder/update"
    const val JOURNAL_REMINDER = "gratitude/journal/reminder"
    const val MUSIC_CATEGORIES = "music/category/list"
    const val MEDITATION_SESSION_DELETE = "meditation/session/delete"
    const val MUSIC_LIST = "category/music/list"
    const val CUSTOMIZE_AFFIRMATION = "guided/affirmation/customise"
    const val EXPLORE = "explore"
    const val EXPLORE_ALL = "explore/all"
    const val MEDITATION_STREAK = "meditation/streak"
    const val MEDITATION_SESSION_HISTORY = "meditation/session/history"
    const val SHARE_URL = "share/url"
    const val MEDITATION_SESSION_DATE = "meditation/session/date"
    const val GUIDED_WISDOM = "wisdom"
    const val QUOTES = "quotes"
    const val NOTIFICATION_LIST = "notification/list"
    const val MY_AFFIRMATION = "my/affirmation/list"
    const val SLEEP = "sleep"
    const val SLEEP_ALL = "sleep/all"
    const val SUBSCRIBE = "subscribe"
    const val SUBSCRIPTION_STATUS = "subscription/status"

    /**
     * Api's constants
     */
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val REASON_ID = "RID"
    const val TITLE = "title"
    const val DEVICE_TOKEN = "deviceToken"
    const val DEVICE_TYPE = "deviceType"
    const val FULL_NAME = "fullName"
    const val COUNTRY = "country"
    const val PROFILE_PICTURE = "profilePicture"
    const val SOCIAL_ID = "socialID"
    const val LOGIN_TYPE = "loginType"
    const val START_TIME = "startTime"
    const val END_TIME = "endTime"
    const val STREAK_COUNT = "streakCount"
    const val INTERVAL = "interval"
    const val DAYS = "days"
    const val CATEGORY = "category"
    const val GENERAL_STATUS = "generalStatus"
    const val TYPE = "type"
    const val NOTIFICATION_READ = "notification/read"
    const val DATA_TYPE = "dataType"
    const val DESCRIPTION = "description"
    const val THUMBNAIL = "thumbnail"
    const val PAGE_NUMBER = "pageNo"
    const val CID = "CID"
    const val SUB_CATEGORY = "subCategory"
    const val SCID = "SCID"
    const val FILE = "file"
    const val BACKGROUND = "background"
    const val AUDIO_NAME = "audioName"
    const val AFFIRMATION = "affirmations"
    const val AFFIRMATION_ID = "affirmationID"
    const val WISDOM_ID = "wisdomID"
    const val FAVOURITE_TYPE = "favouriteType"
    const val STATUS = "status"
    const val QUOTE_ID = "quoteID"
    const val MUSIC_ID = "musicID"
    const val MEDITATION_ID = "meditationID"
    const val SEARCH = "search"
    const val GUIDED_MEDITATION_SESSION_END = "meditation/session/end"
    const val SELF_MEDITATION_SESSION_END = "self/meditation/session/end"
    const val GUIDED_AFFIRMATION_SESSION_END = "affirmation/end"
    const val GUIDED_WISDOM_SESSION_END = "wisdom/session/end"
    const val DATE = "date"
    const val ID = "id"
    const val MONTH = "month"
    const val YEAR = "year"
    const val TIME = "time"
    const val JOURNAL_DAYS = "journalDays"
    const val GRATITUDE_DAYS = "gratitudeDays"
    const val DURATION_STATUS = "durationStatus"
    const val AID = "AID"
    const val DURATION = "duration"
    const val BACKGROUND_DURATION = "backgroundDuration"
    const val MID = "MID"
    const val TRANSCRIPT = "transcript"
    const val AUDIO_TYPE = "audioType"
    const val AUDIO = "audio"
    const val PLAN_TYPE = "planType"
    const val TRANSACTION_ID = "transactionID"
    const val NAME = "name"
    const val MESSAGE = "message"

    enum class UploadType(val value: String) {
        AFFIRMATIONS(value = "affirmations"),
        MUSIC(value = "music"),
        PROFILE_PICTURES(value = "profilePictures"),
        JOURNAL(value = "journals")
    }

    enum class FavouriteType(val value: Int) {
        Affirmation(value = 0),
        Meditation(value = 1),
        Music(value = 2),
        Quote(value = 3),
        Wisdom(value = 4)
    }

    enum class FavouriteStatus(val value: Int) {
        Favourite(value = 1),
        RemoveFavourite(value = 0)
    }

    enum class AffirmationFavouriteType(val value: Int) {
        Text(value = 0),
        MyAffirmation(value = 1),
        Guided(value = 2),
        SleepAffirmation(value = 3)
    }

    enum class MeditationOrMusicFavouriteType(val value: Int) {
        Normal(value = 0),
        Sleep(value = 1)
    }

    enum class CategoryType(val value: Int) {
        TEXT(value = 0),
        GUIDED_AFFIRMATION(value = 1),
        GUIDED_MEDITATION(value = 2)
    }

    enum class Types(val value: Int) {
        JOURNAL(value = 1),
        AFFIRMATION(value = 2),
        QUOTES(value = 3)
    }

    enum class NotificationOnOff(val value: Int) {
        NOTIFICATION_ON(1),
        NOTIFICATION_OFF(0)
    }


    enum class IsBackgroundMusicEnabled(val value: String) {
        ENABLED("1"),
        DISABLED("0")
    }

    enum class ExploreType(val value: String) {
        AFFIRMATION(value = "0"),
        MEDITATION(value = "1"),
        MUSIC(value = "2"),
        QUOTE(value = "3"),
        SLEEP(value = "5"),
        WISDOM(value = "4"),
        SLEEP_AFFIRMATION(value = "2"),
        SLEEP_MEDITATION(value = "3"),
    }

    enum class AffirmationAudioType(val value: String) {
        UPLOAD(value = "1"),
        RECORDED(value = "0")
    }
}
