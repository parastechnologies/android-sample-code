package com.highenergymind.api


/**
 * Created by developer on 04/03/24
 */
object ApiEndPoint {
    const val GET_CATEGORIES = "api/getCategories"
    const val GET_SUB_CATEGORIES = "api/getSubCategories"
    const val REGISTER = "auth/register"
    const val VERIFY_SIGN_UP_OTP = "api/emailVerification"
    const val LOGIN = "auth/login"
    const val GET_PROFILE = "auth/getProfile"
    const val EDIT_PROFILE = "user/editProfile"
    const val FORGOT_PASSWORD = "api/forgotPassword"
    const val CHANGE_PASSWORD = "user/changePassword"
    const val ADD_REMINDER = "user/addReminder"
    const val RESET_PASSWORD = "api/resetPassword"
    const val LOG_OUT = "auth/logOut"
    const val GET_REMINDER = "user/getReminder"
    const val CONTACT_US = "user/postContact"
    const val SOCIAL_REGISTER = "auth/socialRegister"
    const val SOCIAL_LOGIN = "auth/socialLogin"
    const val GET_FAQ = "api/getFaqs"
    const val HOME_DASHBOARD = "user/homeDashboard"
    const val DELETE_ACCOUNT = "auth/deleteAccount"
    const val MARK_FAVOURITE = "user/markFavourite"
    const val GET_FAVOURITE_LIST = "user/getFavouriteList"
    const val GET_AFFIRMATION_BY_TRACK_ID = "user/getAffirmatonByTrackId"
    const val SUGGEST_AFFIRMATION = "user/suggestAffirmate"
    const val EXPLORE_SECTION_API = "user/exploreSection"
    const val DEL_SUB_CATEGORY = "user/delSubCategory"
    const val SEE_ALL_TRACKS = "user/seeallTracks"
    const val UPDATE_USER_CATEGORY = "user/updateUserCategory"
    const val SHARE_INVITATION_CODE = "user/shareInvitationCode"
    const val SEARCH_AFFIRMATION_TRACK = "user/searchAffirmationTrack"
    const val GET_BACKGROUND_AUDIO_LIBRARY = "user/getBackgroundAudioLibrary"
    const val GET_BACKGROUND_THEME_IMAGE_LIBRARY = "user/getBackgroundThemeImageLibrary"
    const val SCROLL_AFFIRMATION = "user/scrollAffirmation"
    const val CHANGE_BACKGROUND_IMAGE = "user/changeAffirmationDayBackgroundImage"
    const val GET_NOTIFICATION_LIST = "user/getNotificationList"
    const val ADD_SUBSCRIPTION_STATUS = "user/addsubscriptionStatus"
    const val SEE_ALL_MUSIC_LIBRARY = "user/seeAllMusicLibrary"
    const val PAGE_CONTENT = "api/getpageContent"
    const val GET_CHOICES = "api/getChoices"
    const val PLAY_TRACK = "user/playTrack"
    const val RECENT_TRACK_LIST = "user/recentTrackList"
    const val FOLLOW_US_LINK = "api/followUsLink"
    const val GET_MUSIC_DETAIL = "user/getBackgroundAudioDetail"
    const val REDEEM_CODE = "user/redeemedCode"
}