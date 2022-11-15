package com.app.muselink.util


object SyncConstants {
    val CONNECT_TIMEOUT = 240L //seconds
    val WRITE_TIMEOUT = 240L//seconds
    val READ_TIMEOUT = 240L//seconds

    const val BASE_URL = "http://ankit.parastechnologies.in/muselink/Api/"
    const val CHAT_IMAGE_BASE_URL = "http://ankit.parastechnologies.in/muselink/uploads/Chat-stuff/"
    const val BASE_URL_IMAGE = "http://ankit.parastechnologies.in/muselink/uploads/user-profile/"
    const val AUDIO_FILE_FULL = "http://ankit.parastechnologies.in/muselink/uploads/full-audio/"
    const val AUDIO_FILE_TRIM = "http://ankit.parastechnologies.in/muselink/uploads/trim-audio/"
    const val WEB_SOCKET_CONNECTION_URL = "ws://208.109.12.159:8089"
    const val FIREBASE_SERVER_KEY = "AAAAi1lSwk8:APA91bGyrKaum2n305ttRpweRMm-MGltXyMsDnvj_tDlBwEPQ4vp04lWvLtlovw58CsTtJnYNZ91XnZstaU4JbxLJ45xESJ1vs4fK07PB0Sfq2d18Lh_MFAShOLwCE49tyFOzsRPeiR3"
    const val FireBase_API_KEY = "key=" + FIREBASE_SERVER_KEY
    const val SIGN_UP_END_POINT = "user/SignUp"
    const val LOGIN_END_POINT = "user/logIn"
    const val SUBSCRIPTION_END_POINT = "Subscription"
    const val SUBSCRIPTION_STATUS_END_POINT = "subscription/checkSubscriptionStatus"
    const val LOGOUT_END_POINT = "user/logOut"
    const val GET_AUDIO_USER_ID = "audio/getAudioAccordingToUserId"
    const val DELETE_AUDIO = "audio/deleteAudio"
    const val CHANGE_NOTIFICATION_STATUS_AUDIO = "audio/changeNotificationStatusForAudio"
    const val GET_PERSONAL_INTEREST_USER_ID = "interest/getPersonalInterestAndCareerGoal"
    const val GET_INTEREST = "interest/getInterests"
    const val UPDATE_PERSONAL_INTEREST_USER_ID = "interest/updatePersonalInterest"
    const val UPDATE_PERSONAL_CAREER_USER_ID = "interest/updatePersonalCareerGoal"
    const val UPDATE_BIOGRAPHY_USER_ID = "setting/editBiography"
    const val DELETE_ACCOUNT_END_POINT = "user/deleteAccount"
    const val ACCOUNT_STATUS_END_POINT = "setting/changeAccountPermissionsStatus"
    const val GET_PROFILE_END_POINT = "user/getProfile"
    const val GET_BLOCK_ACCOUNTS_END_POINT = "setting/listOfBlockAccount"
    const val CHANGE_PASSWORD_END_POINT = "user/changePassword"
    const val UN_BLOCK_ACCOUNTS_END_POINT = "socailnetwork/unBlock"
    const val GET_ALL_AUDIO_END_POINT = "audio/getAllAudio"
    const val GET_AUDIO_UPLOADED_LIMIT = "audio/getAudioUploadCountAccordingToSubscription"
    const val GET_ALL_USER_PROFILE = "user/getAllUserProfiles"
    const val GET_RECENT_CHAT = "socailnetwork/getRecentChatListPost"
    const val GET_CHAT_LISTING = "user/getChatList"
    const val FAVOURITE_USER = "socailnetwork/favoriteUser"
    const val FAVOURITE_AUDIO = "audio/favoriteAudio"
    const val OTP_VERIFICATION_END_POINT = "user/verifyOtp"
    const val FORGET_PASSWORD_END_POINT = "user/forgotPassword"
    const val CHANGE_EMAIL = "setting/changeEmail"
    const val CHANGE_USERNAME = "setting/changeUserName"
    const val ADD_REPORT_AUDIO = "audio/reportOnAudio"
    const val CHANGE_PHONE_NUMBER = "setting/changePhone"
    const val GET_GOALS = "interest/getGoals"
    const val GET_ROLE = "interest/getProjectRoles"
    const val UPLOAD_AUDIO = "audio/upload"
    const val OFFLINE_STATUS = "user/updateOnlineOfflineStatus"
    const val AUDIO_DESCRIPTION = "audio/getAudioDescription"
    const val NOTIFICATION_LIST = "socailnetwork/getNotificationList"
    const val SUPPORT = "setting/support"
    const val UPLOAD_CHAT_STUFF = "user/uploadChatStuff"
    const val REMOVE_MATCHES = "socailnetwork/removeMatch"
    const val SEARCH = "search"
    const val UPLOAD_PROFILE_IMAGE = "user/uploadProfilePicture"
    const val CHNAGE_NOTIFICATION_SETTING = "setting/changeNotificationSetting"
    enum class CredentialTypes(val value: String) {
        LOGIN("login"),
        SIGN_UP("signUp")
    }
    enum class NotificationTypes(val value: String) {
        NEW_ADMIRER("NewAdmirerNotificationStatus"),
        NOTIFICATION_SETTING_STATUS("NotificationSettingStatus"),
        NEW_MESSAGE("NewMessageNotificationStatus"),
        NEW_MATCH("NewMatchFileNotificationStatus")
    }
    enum class AccountStatus(val value: String) {
        ACCOUNT_STATUS("AccountStatus"),
        SOUDN_FILE_STATUS("SoundFileStatus"),
        DIRECT_MESSAGE_STATUS("DirectMessageStatus")
    }
    enum class AuthTypes(val value: String) {
        EMAIL("email"),
        PHONE("phone"),
        SOCIAL("social")
    }
    enum class SupportTypes(val value: String) {
        SUBMIT_A_IDEA("SubmitAIdea"),
        REPORT_A_PROBLEM("ReportAProblem")
    }
    enum class APIParams(val value: String) {
        EMAIL("email"),
        USER_NAME("userName"),
        SUPPORT_FILE("supportFile"),
        PROFILE_PICTURE("profilePicture"),
        SUPPORT_TYPE("supportType"),
        REVIEW("review"),
        USER_ID("userId"),
        RECEIVER_ID("recieverID"),
        FROM_ID("fromId"),
        AUDIO_ID("audioId"),
        AUDIO_IDCAP("AudioId"),
        TO_ID("toId"),
        GOALID("goalId"),
        INTERESTID("interestsId"),
        BIOGRAPHY("biography"),
        OLD_PASSWORD("oldPassword"),
        NEW_PASSWORD("newPassword"),
        CONFIRM_PASSWORD("confirmPassword"),
        BLOCK_ACCOUNT_ID("blockedAccountId"),
        PERMISSION_TYPE("permissionsType"),
        ACCOUNT_STATUS("accountStatus"),
        MESSAGE("message"),
        SOUND_FILE_STATUS("soundFileStatus"),
        DIRECT_MESSAGE_STATUS("directMessageStatus"),
        NOTIFICATION_SETTING_TYPE("notificationSettingType"),
        STATUS_VALUE("statusValue"),
        STATUS("status"),
        UPLOAD_FILE_SINGLE("uploadFileSingle"),
        UPLOAD_FILE("uploadFile"),
        PASSWORD("password"),
        LOGIN_TYPE("logInType"),
        SIGNUP_TYPE("SignUpType"),
        PAYMENT_ID("paymentId"),
        TRANSACTION_ID("transactionId"),
        AMOUNT("amount"),
        PRODUCT("Product"),
        VERIFY_TYPE("verifyType"),
        PHONE("phone"),
        SOCIAL_ID("Socailid"),
        OTP("otp"),
        DEVICE_TYPE("deviceType"),
        DEVICE_TOKEN("deviceToken"),
        COUNTRY_NAME("countryName")
    }
}