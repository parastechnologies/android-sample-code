package com.app.muselink.constants

object AppConstants {
    const val APP_NAME = "Muselink"
    const val FIRST_WALK_THROUGH = "FIRST_WALK_THROUGH"
    const val SECOND_WALK_THROUGH = "SECOND_WALK_THROUGH"
    const val THIRD_WALK_THROUGH = "THIRD_WALK_THROUGH"

    enum class FormErrors {
        MISSING_EMAIL,
        INVALID_EMAIL,
        MISSING_MESSAGE,
        INVALID_USERNAME,
        MISSING_PHONE,
        MISSING_REVIEW,
        MISSING_BIOGRAPHY,
        MISSING_GOALS,
        MISSING_INTERESTS,
        MISSING_OTP,
        INVALID_PHONE,
        MISSING_PASSWORD,
        MISSING_OLD_PASSWORD,
        MISSING_NEW_PASSWORD,
        MISSING_CONFIRM_PASSWORD,
        INVALID_PASSWORD,
        INVALID_NEW_PASSWORD
    }


    val NOTIFICATION_CHANNEL_ORDER_PLACED: String = "OrderPlaced"
    val NOTIFICATION_CHANNEL_ORDER_SUCCESSFUL: String = "PaymentSuccessful"
    val NOTIFICATION_CHANNEL_ORDER_FAIL: String = "PaymentSuccessful"
    const val FILE = "file:"
    //Test
    const val profile_image_url = ""
    val SELECTED_ADDRESS = 1771
    val CONNECT_TIMEOUT = 50L //seconds
    val WRITE_TIMEOUT = 30L//seconds
    val READ_TIMEOUT = 30L//seconds
    val SPLASH_TIMEOUT = 4000L //millisecs
    const val CAMERA_REQUEST = 1
    const val PROFILE_IMAGE_REQUEST = 3
    const val PERMISSION_SETTING_REQUEST_CODE = 2
    const val SELECTED_IMG_CROP = 203
    const val EDITPROFILE_REQUEST_CODE = 4
    const val EDITITEM_REQUEST_CODE = 6
    const val ADD_CARD_REQUEST_CODE = 5
    const val CURRENT_ORDER_REQUEST_CODE = 7
    const val UPLOAD_VEHICLE_DOCUMENT_REQUEST_CODE = 8
    const val UPLOAD_VEHICLE_PHOTOS_REQUEST_CODE = 9
    const val STATUS_REQUEST_CODE = 10
    const val PROOF_SELECTION = 12
    val AUTOCOMPLETE_REQUEST_CODE = 2342
    const val GOOGLE_PLUS_REQUEST_CODE = 102
    const val CHOOSE_PHOTO_INTENT=106

    const val Bussiness_Address_LATITUDE = "bussiness_address_latitude"
    const val Bussiness_Address_LONGITUDE = "bussiness_address_longitude"
    val ADDDRESS = "address"

    val NOTIFICATION_CHANNEL: String = "notification_server"
    val NOTIFICATION_CHANNEL_SERVICE: String = "BookingService"
    const val PREF_FCM_REGISTERED = "fcm_registered"
    const val AddBankBottomsheet = "AddBankBottomsheet"

    val PERSONAL_DETAILS = "Personal Details"
    val VEHICLE_DETAILS = "Vehicle Details"

    val TERMS_CONDITION = "TermsCondition"
    val PRIVACY_POLICY = "PrivacyPolicy"
    val ABOUT_US = "AboutUs"

    val BROADCAST_NOTIFICATION_COUNT = "notification_count_broad"
    val BROADCAST_NEW_ORDER = "new_order"

    //prefs
    const val PREF_DEVICE_TOKEN = "device_token"
    const val PREF_DEVICE_TYPE = "device_type"
    const val PREFS_UID = "user_id"

    const val MOBILE_NUM = "mobile_number"

    const val SPACE_VERTICAL_RECYCLEVIEW = 32

    const val ACCEPT_ORDER = 1
    const val REJECT_ORDER = 10
    const val PICK_UP_ORDER = 2
    const val ON_THE_WAY = 3
    const val DELIVERED = 4
    const val CANCEL_ORDER = 5

    const val DashBoardFragmentTag = "DashBoardFragment"
    const val ProfileFragmentTag = "ProfileFragment"
    const val OrderManagementFragmentTag = "OrderManagementFragment"

    const val NotificationFragmentTag = "NotificationFragment"
    const val ContactUsFragmentTag = "ContactUsFragment"

    const val STORAGE_PATHS = "storagepaths"
    const val DRIVER_DETAILS = "driverDetails"

    const val DisputeDialogTag = "DisputeDialog"
    const val WinDialogFragmentFragmentTag = "WinDialogFragment"
    const val ChatsFragmentTag = "ChatsFragment"
    const val MyBetsFragmentTag = "MyBetsFragment"
    const val LeaderBoardFragmentTag = "LeaderBoardFragmentTab"
    const val MyGamesFragmentTag = "MyGameFragment"
    const val MenuManagementFragmentTag = "MenuManagementFragment"
    const val CurrentOrderFragmentTag = "CurrentOrderFragment"
    const val PastOrderFragmentTag = "PastOderFragment"

    const val IngredientsBottomDialogFragmentTag = "IngredientsBottomDialogFragment"
    const val AddMenuImageBottomDialogFragmentTag = "AddMenuImageBottomDialogFragmentTag"
    const val AddIngredientBottomDialogFragmentTag = "AddIngredientBottomDialogFragmentTag"
    const val CancelOrderBottomDialogFragmentTag = "CancelOrderBottomDialogFragmentTag"
    const val AddTimingBottomDialogFragmentTag = "AddTimingBottomDialogFragmentTag"

    const val select_day = " Select Day"

    const val OrderType = "OrderType"
    const val SELECTION_TYPE = "selection_type"
    const val CURRENT_LATITUDE = "current_latitude"
    const val CURRENT_LONGITUDE = "current_longitude"
    const val MENU_ITEM = "menu_item"
    const val MENU_ITEM_ID = "menu_item_id"
    const val MENU_ITEM_DELETED = "menu_item_deleted"

    const val ORDER_DETAILS = "order_details"
    const val ORDER_ID = "order_id"
    const val CALL_FROM = "order_details"
    const val RATING = "RatingScreen"

    const val COMPLETED_JOB = "CompletedJob"
    const val WORKINPROGRESS = "WorkInProgress"
    const val CANCELEDJOB = "Canceled_job"

    const val CATEGORY_DETAILS = "category_details"
    const val SERVICE_PROVIDER = "service_provider"
    const val VIDEO_LINK = "video_link"
    const val SERVICE_PROVIDER_LIST = "service_provider_list"
    const val BOOKING_DETAILS = "booking_details"
    const val SCREEN_TYPE = "screen_type"
    const val PAGER_TAB_POSITION = "pagerTabPosition"
    const val BOOKING = "booking"
    const val REVIEW_LIST = "review_list"

    const val COMPLETED_FRAGMENT = "completed_fragment"
    const val CANCEL_FRAGMENT = "cancel_fragment"

    const val SERVICE_TITLE = "service_title"
    const val AMOUNT = "amount"
    const val NAME = "name"
    const val DETAIL_DESC = "detail_desc"
    const val LOCATION = "location"
    const val PHONE_NUMBER = "phone_number"
    const val DATE = "date"
    const val TIME = "time"
    const val START_TIME = "start_time"
    const val END_TIME = "end_time"
    const val SERVICE_ID = "service_id"
    const val PRICE = "price"
    const val SERVICE_PROVIDER_ID = "serviceProvider_id"
    const val BOOKED_SERVICE_TYPE = "bookedServiceType "
    const val SERVICE_LISTING = "servicelisting"

    const val REGISTER_DETAILS = "register_details"
    const val REFRESH_LIST = "refresh_list"
    const val RECENT_CHAT_MODAL = "recent_chat_modal"
    const val MATCH_SCREEN = "MATCH_SCREEN"
    const val receiverId = "receiverId"
    const val DM = "DM"
    const val TITLE = "TITLE"
    const val TYPE = "TYPE"
    const val receiverName = "receiverName"
    const val message = "message"
    const val IS_ORDER_ACCEPTED = "is_order_accepted"
    const val SELECTED_PROOFS = "selected_proofs"

    val REJECTED = "Rejected"
    val COMPLETED = "Completed"
    val STORE_DETAILS = "storeDetails"
    val REQUEST_QUEUE = "Requests Queue"
    val NOTIFICATION_LIST = "notification_list"
    val ACCEPTED_REQUEST = "Accepted Request"
    val HISTORY_FRAGMENT = "History Fragment"

    const val PREFS_NAME = "name"
    const val User_Name = "username"
    const val PREFS_NOTIFICATION_COUNT = "notification_count"
    const val PREFS_TOKEN_ID = "tokenid"
    const val PREFS_GENDER = "gender"
    const val PREFS_ACCOUNT_STATUS = "account_status"
    const val PREFS_REMEMBERME = "remember_pass"
    const val PREFS_PASS = "password"
    const val PREFS_EMAIL = "email"
    const val PREFS_LOGIN_TYPE = "login_type"
    const val PREFS_EMAIL_STATUS = "email_status"
    const val PREFS_CREATED_DATE = "created_date"
    const val PREFS_FIRSTNAME = "firstname"
    const val PREFS_PHONE = "phone"
    const val PREFS_LASTNAME = "lastname"
    const val PREFS_USER_ID = "userid"
    const val PREFS_CHAT_UNIQUE_NO = "chat_unique_no"
    const val PREFS_SUBSCRIPTION_STATUS = "subscripton_status"
    const val PREFS_USER_NAME = "username"
    const val PREFS_USER_TYPE = "user_type"
    const val PREFS_LANGUAGE_SELECTION = "language_selection"
    const val PREFS_RESTAURANT_CREATED = "restaurant_created"
    const val PREFS_PROFILE_CREATED = "profile_created"
    const val PREFS_PROFILE_APPROVAL = "profile_approval"
    const val PREFS_RESTAURANT_ID = "restaurant_id"
    const val PREFS_DRIVER_ID = "driver_id"
    const val PREFS_MUSIC_TUTORIAL_1 = "PREFS_MUSIC_TUTORIAL_1"
    const val PREFS_MUSIC_TUTORIAL_2 = "PREFS_MUSIC_TUTORIAL_2"

    const val EMAIL_ID = "email_id"
    const val USER_DETIALS = "user_details"
    const val INSTAGRAM_SOCIAL_ID = "instagram_social_id"
    const val INSTAGRAM_ACCESS_TOKEN = "instagram_access_token"

    const val PREFS_LOCATION = "location"
    const val PREFS_PROFILE_IMAGE = "profile_image"
    const val IS_FIRST_TIME = "IS_FIRST_TIME"
    const val COUNTRY_NAME = "COUNTRY_NAME"

    enum class UserType {
        Restaurant,
        Driver,
        Customer
    }

    enum class AccountType(val value: String) {
        CUSTOMER("1"),
        SERVICE_PROVIDER("2")
    }

    enum class SongType(val value: String) {
        TRIM("trim"),
        FULL("full")
    }

    enum class LoginType(val value: String) {
        SOUND_FILE("SoundFile"),
        USERPROFILE("UserProfile")
    }

    enum class GENDER {
        Male,
        Female
    }

    enum class ServiceStatus(val value: String?) {
        active("1"),
        cancelled("2"),
        completed("3"),
        accepted("4"),
        started("5"),
        rejected("6"),
        on_the_way("7"),
        left_from_home("8"),
        service_photos("9"),
        review("10"),
    }

    enum class DocumentType(val value: String) {
        AURTRALIAN_LICENCE("australian_licence"),
        INTERNATIONAL_LICENCE("international_licence"),
        AUSTRALIAN_PASSSPORT("australian_passsport"),
        NON_AUSTRALIAN_PASSSPORT("non_australian_passport")
    }

    enum class SettingTypes(val value: String) {
        NOTIFICATION("notify"),
        AVAILABILITY("avail")
    }

    enum class BookedServiceType(val value: String) {
        FIXED("fixed"),
        PRICE_PER_HOUR("pricePerHour")
    }

    enum class UserLanguageSelection(val value: String) {
        English("en"),
        Italian("it")
    }

    enum class PaymenType {
        cash_on_delivery,
        card
    }

    enum class OrderTypeData(val value: String) {
        PRE_ORDER("pre_order")
    }

}