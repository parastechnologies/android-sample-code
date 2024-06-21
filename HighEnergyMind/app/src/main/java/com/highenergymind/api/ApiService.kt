package com.highenergymind.api

import com.highenergymind.base.BaseResponse
import com.highenergymind.data.BackgroundThemeResponse
import com.highenergymind.data.ExploreSectionResponse
import com.highenergymind.data.FavResponse
import com.highenergymind.data.FollowUsResponse
import com.highenergymind.data.GetCategoriesResponse
import com.highenergymind.data.GetChoiceResponse
import com.highenergymind.data.GetFaqResponse
import com.highenergymind.data.GetMusicResponse
import com.highenergymind.data.GetSubCategoryResponse
import com.highenergymind.data.GetTrackAffirmationResponse
import com.highenergymind.data.HomeDashboardResponse
import com.highenergymind.data.MusicDetailResponse
import com.highenergymind.data.NotificationResponse
import com.highenergymind.data.PageContentResponse
import com.highenergymind.data.RecentListResponse
import com.highenergymind.data.ScrollAffirmationResponse
import com.highenergymind.data.SearchResponse
import com.highenergymind.data.SeeAllMusicResponse
import com.highenergymind.data.SeeAllResponse
import com.highenergymind.data.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface ApiService {
    @GET(ApiEndPoint.GET_CATEGORIES)
    suspend fun getCategories(): Response<GetCategoriesResponse>

    @POST(ApiEndPoint.GET_SUB_CATEGORIES)
    suspend fun getSubCategory(@Body map: HashMap<String, Any>): Response<GetSubCategoryResponse>

    @POST(ApiEndPoint.REGISTER)
    suspend fun register(@Body map: HashMap<String, Any>): Response<UserResponse>

    @POST(ApiEndPoint.VERIFY_SIGN_UP_OTP)
    suspend fun verifySignUpOtp(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.LOGIN)
    suspend fun loginApi(@Body map: HashMap<String, Any>): Response<UserResponse>

    @POST(ApiEndPoint.GET_PROFILE)
    suspend fun getProfile(): Response<UserResponse>

    @Multipart
    @POST(ApiEndPoint.EDIT_PROFILE)
    suspend fun editProfile(
        @Part img: MultipartBody.Part? = null,
        @PartMap map: HashMap<String, RequestBody>
    ): Response<UserResponse>

    @POST(ApiEndPoint.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body map: HashMap<String, Any>): Response<UserResponse>

    @POST(ApiEndPoint.CHANGE_PASSWORD)
    suspend fun changePassword(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.ADD_REMINDER)
    suspend fun addReminder(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.RESET_PASSWORD)
    suspend fun resetPassword(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.LOG_OUT)
    suspend fun logOut(): Response<BaseResponse>

    @POST(ApiEndPoint.GET_REMINDER)
    suspend fun getReminder(): Response<UserResponse>

    @POST(ApiEndPoint.CONTACT_US)
    suspend fun contactUsApi(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.SUGGEST_AFFIRMATION)
    suspend fun suggestAffirmation(@Body map: HashMap<String, Any>): Response<BaseResponse>


    @POST(ApiEndPoint.SOCIAL_REGISTER)
    suspend fun socialRegister(@Body map: HashMap<String, Any>): Response<UserResponse>

    @POST(ApiEndPoint.SOCIAL_LOGIN)
    suspend fun socialLogin(@Body map: HashMap<String, Any>): Response<UserResponse>

    @GET(ApiEndPoint.GET_FAQ)
    suspend fun getFaq(): Response<GetFaqResponse>

    @POST(ApiEndPoint.HOME_DASHBOARD)
    suspend fun homeDashboardApi(): Response<HomeDashboardResponse>

    @POST(ApiEndPoint.DELETE_ACCOUNT)
    suspend fun deleteAccountApi(): Response<BaseResponse>

    @POST(ApiEndPoint.MARK_FAVOURITE)
    suspend fun markFavourite(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.GET_FAVOURITE_LIST)
    suspend fun getFavList(@Body map: HashMap<String, Any>): Response<FavResponse>

    @POST(ApiEndPoint.GET_AFFIRMATION_BY_TRACK_ID)
    suspend fun getAffirmationByTrackId(@Body map: HashMap<String, Any>): Response<GetTrackAffirmationResponse>

    @POST(ApiEndPoint.EXPLORE_SECTION_API)
    suspend fun exploreSectionApi(): Response<ExploreSectionResponse>

    @POST(ApiEndPoint.DEL_SUB_CATEGORY)
    suspend fun deleteSubCategory(@Body map: HashMap<String, Any>): Response<GetCategoriesResponse>

    @POST(ApiEndPoint.SEE_ALL_TRACKS)
    suspend fun seeAllTrack(@Body map: HashMap<String, Any>): Response<SeeAllResponse>

    @POST(ApiEndPoint.UPDATE_USER_CATEGORY)
    suspend fun updateUserCategory(@Body map: HashMap<String, Any>): Response<GetCategoriesResponse>

    @POST(ApiEndPoint.SEARCH_AFFIRMATION_TRACK)
    suspend fun searchAffirmationTrack(@Body map: HashMap<String, Any>): Response<SearchResponse>

    @POST(ApiEndPoint.GET_BACKGROUND_AUDIO_LIBRARY)
    suspend fun getMusicLibrary(@Body map: HashMap<String, Any>): Response<GetMusicResponse>

    @POST(ApiEndPoint.GET_BACKGROUND_THEME_IMAGE_LIBRARY)
    suspend fun getThemeLibrary(): Response<BackgroundThemeResponse>

    @POST(ApiEndPoint.SCROLL_AFFIRMATION)
    suspend fun scrollAffirmation(@Body map: HashMap<String, Any>): Response<ScrollAffirmationResponse>

    @POST(ApiEndPoint.CHANGE_BACKGROUND_IMAGE)
    suspend fun changeBackgroundImage(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.GET_NOTIFICATION_LIST)
    suspend fun getNotificationList(@Body map: HashMap<String, Any>): Response<NotificationResponse>

    @POST(ApiEndPoint.ADD_SUBSCRIPTION_STATUS)
    suspend fun addSubscriptionStatus(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.SEE_ALL_MUSIC_LIBRARY)
    suspend fun seeAllMusicApi(@Body map: HashMap<String, Any>): Response<SeeAllMusicResponse>

    @POST(ApiEndPoint.PAGE_CONTENT)
    suspend fun pageContentApi(@Body map: HashMap<String, Any>): Response<PageContentResponse>

    @GET(ApiEndPoint.GET_CHOICES)
    suspend fun getChoices(): Response<GetChoiceResponse>

    @POST(ApiEndPoint.PLAY_TRACK)
    suspend fun playTrack(@Body map: HashMap<String, Any>): Response<BaseResponse>

    @POST(ApiEndPoint.RECENT_TRACK_LIST)
    suspend fun recentTrackList(@Body map: HashMap<String, Any>): Response<RecentListResponse>

    @POST(ApiEndPoint.FOLLOW_US_LINK)
    suspend fun followUsLink(): Response<FollowUsResponse>


    @POST(ApiEndPoint.GET_MUSIC_DETAIL)
    suspend fun getMusicDetailApi(@Body map: HashMap<String, Any>): Response<MusicDetailResponse>

    @POST(ApiEndPoint.REDEEM_CODE)
    suspend fun redeemCodeApi(@Body map: HashMap<String, Any>): Response<UserResponse>
}