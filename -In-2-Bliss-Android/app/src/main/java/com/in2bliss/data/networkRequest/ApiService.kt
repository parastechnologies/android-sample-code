package com.in2bliss.data.networkRequest

import com.in2bliss.base.BaseResponse
import com.in2bliss.data.model.AdminAffirmationResponse
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.data.model.ChooseBackgroundResponse
import com.in2bliss.data.model.EditProfileResponse
import com.in2bliss.data.model.FavouritesResponse
import com.in2bliss.data.model.GetNotificationStatusResponse
import com.in2bliss.data.model.GetShareDataResponse
import com.in2bliss.data.model.HomeResponse
import com.in2bliss.data.model.LogInResponse
import com.in2bliss.data.model.MeditationTrackerDateHistoryResponse
import com.in2bliss.data.model.MeditationTrackerHistoryResponse
import com.in2bliss.data.model.MeditationTrackerStreakResponse
import com.in2bliss.data.model.NotificationListResponse
import com.in2bliss.data.model.ProfileDetailResponse
import com.in2bliss.data.model.ProfileResponse
import com.in2bliss.data.model.QuotesResponseModel
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.model.ShareResponse
import com.in2bliss.data.model.StepOneResponse
import com.in2bliss.data.model.TextAffirmationListResponse
import com.in2bliss.data.model.journalList.GuidedJournalListResponse
import com.in2bliss.data.model.journalStreak.JournalStreak
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.model.subscription.Subscription
import com.in2bliss.data.model.uploadData.Upload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface ApiService {

    /**
     * Used to complete Step One || Select the particular reason
     */
    @GET(ApiConstant.SIGNUP_REASONS)
    suspend fun signupReasons(): Response<StepOneResponse>

    /**
     * Used to get weekly meditation streak
     */

    @GET(ApiConstant.MEDITATION_STREAK)
    suspend fun meditationStreak(): Response<MeditationTrackerStreakResponse>

    /**
     * Used to complete Step Two || SignUp flow
     */
    @FormUrlEncoded
    @POST(ApiConstant.SIGNUP)
    suspend fun signUp(@FieldMap body: HashMap<String, String>): Response<LogInResponse>


    @FormUrlEncoded
    @POST(ApiConstant.FAVOURITE_LIST)
    suspend fun favourites(@FieldMap body: HashMap<String, String>): Response<FavouritesResponse>

    @FormUrlEncoded
    @POST(ApiConstant.ADMIN_AFFIRMATION)
    suspend fun adminAffirmation(@FieldMap body: HashMap<String, String>): Response<AdminAffirmationResponse>

    @FormUrlEncoded
    @POST(ApiConstant.SHARE_URL)
    suspend fun shareUrl(@FieldMap body: HashMap<String, String>): Response<ShareResponse>

    /**
     * Used to complete Step Two || SignUp flow
     */
    @FormUrlEncoded
    @POST(ApiConstant.MEDITATION_SESSION_HISTORY)
    suspend fun meditationTrackerHistory(@FieldMap body: HashMap<String, String>): Response<MeditationTrackerHistoryResponse>

    @FormUrlEncoded
    @POST(ApiConstant.MEDITATION_SESSION_DELETE)
    suspend fun meditationTrackerDeleteHistory(@FieldMap body: HashMap<String, String>): Response<BaseResponse>


    @FormUrlEncoded
    @POST(ApiConstant.MEDITATION_SESSION_DATE)
    suspend fun meditationTrackerDateHistory(@FieldMap body: HashMap<String, String>): Response<MeditationTrackerDateHistoryResponse>


    /**
     * Used toJOURNAL_ADD
     */
    @FormUrlEncoded
    @POST(ApiConstant.JOURNAL_ADD)
    suspend fun addGuidedJournal(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    /**
     * Used to complete Step Two || SignIn flow->Normal
     */
    @FormUrlEncoded
    @POST(ApiConstant.LOGIN)
    suspend fun logIn(@FieldMap body: HashMap<String, String>): Response<LogInResponse>

    @FormUrlEncoded
    @POST(ApiConstant.GUIDED_MEDIATION)
    suspend fun homeGuidedMeditation(@FieldMap body: HashMap<String, String>): Response<MusicList>

    @FormUrlEncoded
    @POST(ApiConstant.SLEEP)
    suspend fun homeGuidedSleep(@FieldMap body: HashMap<String, String>): Response<MusicList>

    @FormUrlEncoded
    @POST(ApiConstant.GUIDED_AFFIRMATION)
    suspend fun homeGuidedAffirmation(@FieldMap body: HashMap<String, String>): Response<MusicList>

    @FormUrlEncoded
    @POST(ApiConstant.GUIDED_WISDOM)
    suspend fun homeGuidedWisdom(@FieldMap body: HashMap<String, String>): Response<MusicList>

    @FormUrlEncoded
    @POST(ApiConstant.MY_AFFIRMATION)
    suspend fun myAffirmation(@FieldMap body: HashMap<String, String>): Response<MusicList>


    @GET(ApiConstant.PROFILE_DETAIL)
    suspend fun getProfileDetail(): Response<ProfileDetailResponse>

    @GET(ApiConstant.NOTIFICATION_STATUS)
    suspend fun getNotificationStatus(): Response<GetNotificationStatusResponse>

    /**
     * Used to complete Step Two || SignIn flow->Social LogIn
     */
    @FormUrlEncoded
    @POST(ApiConstant.SOCIAL_LOGIN)
    suspend fun socialLogIn(@FieldMap body: HashMap<String, String>): Response<LogInResponse>

    /**
     * Used to complete Step Three || Will add full name
     */
    @FormUrlEncoded
    @POST(ApiConstant.ADD_NAME)
    suspend fun addName(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.SELF_MEDITATION_SESSION_END)
    suspend fun selfMeditationSessionEnd(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    /**
     * Used to complete Step Four || Will add profile image
     */
    @Multipart
    @POST(ApiConstant.PICTURE_ADD)
    suspend fun addProfilePicture(@Part imageFile: MultipartBody.Part): Response<ProfileResponse>

    /**
     * Used to complete Step Five || Get category list
     */
    @FormUrlEncoded
    @POST(ApiConstant.CATEGORY_LIST)
    suspend fun categoryList(
        @FieldMap body: HashMap<String, String>
    ): Response<CategoryResponse>

    @FormUrlEncoded
    @POST(ApiConstant.SHARE_DATA)
    suspend fun getSharedData(
        @FieldMap body: HashMap<String, String>
    ): Response<GetShareDataResponse>


    @GET(ApiConstant.Home)
    suspend fun getHomeData(): Response<HomeResponse>

    /**
     * Used to complete Step Five || Get category list
     */
    @FormUrlEncoded
    @POST(ApiConstant.NOTIFICATION_READ)
    suspend fun readNotification(
        @FieldMap body: HashMap<String, String>
    ): Response<BaseResponse>

    /**
     * Used to complete Step Five further || Add subcategory list
     */
    @FormUrlEncoded
    @POST(ApiConstant.CATEGORY_ADD)
    suspend fun addSubCategory(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    /**
     * Will add reminder
     */
    @FormUrlEncoded
    @POST(ApiConstant.REMINDER_SET)
    suspend fun setReminder(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.JOURNAL_REMINDER)
    suspend fun setJournalReminder(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.REMINDER_UPDATE)
    suspend fun updateReminder(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    /**
     * Will add affirmation
     * Working on the base of type
     * if type(0) just use description , category & image -> Hit another api [Still not provide by backend]
     * else -> category, title, description, transcript, audioType, audio, background
     */
    @FormUrlEncoded
    @POST(ApiConstant.ADD_AFFIRMATION)
    suspend fun addTextAffirmation(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.AFFIRMATION_UPDATE)
    suspend fun updateTextAffirmation(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    /**
     * Will show the list of text affirmation
     */
    @FormUrlEncoded
    @POST(ApiConstant.TEXT_AFFIRMATION_LIST)
    suspend fun textAffirmationList(@FieldMap body: HashMap<String, String>): Response<AffirmationListResponse>


    @FormUrlEncoded
    @POST(ApiConstant.ADMIN_AFFIRMATION_LIST)
    suspend fun myTextAffirmationList(@FieldMap body: HashMap<String, String>): Response<TextAffirmationListResponse>


    @FormUrlEncoded
    @POST(ApiConstant.QUOTES)
    suspend fun quotesList(
        @FieldMap body: HashMap<String, String>
    ): Response<QuotesResponseModel>

    @FormUrlEncoded
    @POST(ApiConstant.GRATITUDE_LIST)
    suspend fun guidedJournal(@FieldMap body: HashMap<String, String>): Response<GuidedJournalListResponse>

    @FormUrlEncoded
    @POST(ApiConstant.MEDITATION_ALL)
    suspend fun meditationSeeAll(@FieldMap body: HashMap<String, String>): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST(ApiConstant.MY_AFFIRMATION_SEE_ALL)
    suspend fun myAffirmationSeeAll(@FieldMap body: HashMap<String, String>): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST(ApiConstant.WISDOM_ALL)
    suspend fun wisdomSeeAll(@FieldMap body: HashMap<String, String>): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST(ApiConstant.AFFIRMATION_ALL)
    suspend fun guidedAffirmationSeeAll(@FieldMap body: HashMap<String, String>): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST(ApiConstant.EXPLORE_ALL)
    suspend fun exploreSeeALl(@FieldMap body: HashMap<String, String>): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST(ApiConstant.SLEEP_ALL)
    suspend fun sleepSeeALl(@FieldMap body: HashMap<String, String>): Response<SeeAllResponse>

    @FormUrlEncoded
    @POST(ApiConstant.NOTIFICATION_LIST)
    suspend fun getNotificationList(@FieldMap body: HashMap<String, String>): Response<NotificationListResponse>

    @Multipart
    @POST(ApiConstant.UPLOAD)
    suspend fun uploadData(
        @Part imageFile: MultipartBody.Part,
        @PartMap body: HashMap<String, RequestBody>
    ): Response<Upload>

    @Multipart
    @POST(ApiConstant.PROFILE_UPDATE)
    suspend fun editProfile(
        @Part imageFile: MultipartBody.Part,
        @PartMap body: HashMap<String, RequestBody>
    ): Response<EditProfileResponse>

    @FormUrlEncoded
    @POST(ApiConstant.DELETE_AFFIRMATION)
    suspend fun deleteAffirmation(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.FAVOURITE_AFFIRMATION)
    suspend fun favouriteAffirmation(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.GUIDED_MEDITATION_SESSION_END)
    suspend fun guidedMeditationSessionEnd(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.GUIDED_AFFIRMATION_SESSION_END)
    suspend fun guidedAffirmationSessionEnd(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.GUIDED_WISDOM_SESSION_END)
    suspend fun guidedWisdomSessionEnd(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @POST(ApiConstant.LOGOUT)
    suspend fun logout(): Response<BaseResponse>

    @POST(ApiConstant.ACCOUNT_DELETE)
    suspend fun accountDelete(): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.JOURNAL_BACKGROUND_IMAGES)
    suspend fun chooseJournalBackground(@FieldMap body: HashMap<String, String>): Response<ChooseBackgroundResponse>

    @FormUrlEncoded
    @POST(ApiConstant.AFFIRMATION_BACKGROUND_IMAGES)
    suspend fun chooseAffirmationBackground(@FieldMap body: HashMap<String, String>): Response<ChooseBackgroundResponse>


    @FormUrlEncoded
    @POST(ApiConstant.DELETE_JOURNAL)
    suspend fun deleteJournal(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.JOURNAL_STREAK)
    suspend fun journalStreak(@FieldMap body: HashMap<String, String>): Response<JournalStreak>

    @FormUrlEncoded
    @POST(ApiConstant.EDIT_JOURNAL)
    suspend fun editJournal(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.EXPLORE)
    suspend fun getExplore(@FieldMap body: HashMap<String, String>): Response<MusicList>

    @FormUrlEncoded
    @POST(ApiConstant.NOTIFICATION_STATUS_SET)
    suspend fun setNotificationStatus(@FieldMap body: HashMap<String, String>): Response<BaseResponse>

    @GET(ApiConstant.MUSIC_CATEGORIES)
    suspend fun getMusicCategories(): Response<MusicCategories>

    @FormUrlEncoded
    @POST(ApiConstant.MUSIC_LIST)
    suspend fun getMusicList(
        @FieldMap body: HashMap<String, String>
    ): Response<com.in2bliss.data.model.musicCateogries.MusicList>

    @FormUrlEncoded
    @POST(ApiConstant.CUSTOMIZE_AFFIRMATION)
    suspend fun customizeAffirmation(
        @FieldMap body: HashMap<String, String>
    ): Response<BaseResponse>


    @FormUrlEncoded
    @POST(ApiConstant.CONTACT_US)
    suspend fun contactUs(
        @FieldMap body: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.ADD_AFFIRMATION)
    suspend fun addSpokenAffirmation(
        @FieldMap body: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.AFFIRMATION_UPDATE)
    suspend fun updateSpokenAffirmation(
        @FieldMap body: HashMap<String, String>
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(ApiConstant.SUBSCRIBE)
    suspend fun subscribe(
        @FieldMap hashMap: HashMap<String, String>
    ): Response<BaseResponse>

    @GET(ApiConstant.SUBSCRIPTION_STATUS)
    suspend fun getSubscriptionStatus(
    ): Response<Subscription>
}

