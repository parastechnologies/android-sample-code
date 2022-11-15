package com.app.muselink.retrofit

import com.app.muselink.data.modals.responses.*
import com.app.muselink.data.modals.responses.getDescription.GetDescriptionResponseModel
import com.app.muselink.data.modals.responses.getInterest.GetInterestResponseModel
import com.app.muselink.data.modals.responses.notification.NotificationListResponseModel
import com.app.muselink.model.responses.*
import com.app.muselink.model.responses.chat.UploadChatFileResponseModel
import com.app.muselink.ui.fragments.chatscreen.modals.ChatListRes
import com.app.muselink.util.SyncConstants
import com.google.gson.JsonArray
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface ApiServices {

    @FormUrlEncoded
    @POST(SyncConstants.LOGIN_END_POINT)
    suspend fun login(@FieldMap request: HashMap<String, String>): Response<LoginResponse>

    @FormUrlEncoded
    @POST(SyncConstants.SUBSCRIPTION_END_POINT)
    suspend fun subscription(@FieldMap request: HashMap<String, String>): Response<SubscriptionRes>

    @FormUrlEncoded
    @POST(SyncConstants.SUBSCRIPTION_STATUS_END_POINT)
    suspend fun subscriptionStatus(@FieldMap request: HashMap<String, String>): Response<SubscriptionStatusRes>

    @FormUrlEncoded
    @POST(SyncConstants.LOGOUT_END_POINT)
    suspend fun logoutUser(@FieldMap request: HashMap<String, Any>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.GET_AUDIO_USER_ID)
    suspend fun getSoundFiles(@FieldMap request: HashMap<String, String>): Response<GetSoundFileProfileRes>

    @FormUrlEncoded
    @POST(SyncConstants.DELETE_AUDIO)
    suspend fun deleteAudio(@FieldMap request: HashMap<String, String>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.CHANGE_NOTIFICATION_STATUS_AUDIO)
    suspend fun chnageNotificationSatusAudio(@FieldMap request: HashMap<String, String>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.GET_PERSONAL_INTEREST_USER_ID)
    suspend fun getpersonalInterests(@FieldMap request: HashMap<String, Any>): Response<GetInterestsGoalsRes>


    @FormUrlEncoded
    @POST(SyncConstants.GET_INTEREST)
    suspend fun getInterests(@FieldMap request: HashMap<String, Any>): Response<GetInterestsRes>

    @FormUrlEncoded
    @POST(SyncConstants.GET_INTEREST)
    suspend fun getEditInterests(@FieldMap request: HashMap<String, Any>): Response<GetInterestResponseModel>

    @FormUrlEncoded
    @POST(SyncConstants.GET_GOALS)
    suspend fun getGoals(@FieldMap request: HashMap<String, Any>): Response<GetGoalsResponseModel>

    @FormUrlEncoded
    @POST(SyncConstants.UPDATE_PERSONAL_INTEREST_USER_ID)
    suspend fun updatepersonalInterests(@FieldMap request: HashMap<String, Any>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.UPDATE_PERSONAL_CAREER_USER_ID)
    suspend fun updatepersonalCareerGoals(@FieldMap request: HashMap<String, Any>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.UPDATE_BIOGRAPHY_USER_ID)
    suspend fun updateBiography(@FieldMap request: HashMap<String, Any>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.DELETE_ACCOUNT_END_POINT)
    suspend fun deleteAccount(@FieldMap request: HashMap<String, Any>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.ACCOUNT_STATUS_END_POINT)
    suspend fun setAccountStatus(@FieldMap request: HashMap<String, Any>): Response<AccountStatusRes>

    @FormUrlEncoded
    @POST(SyncConstants.GET_PROFILE_END_POINT)
    suspend fun getProfileDetails(@FieldMap request: HashMap<String, Any>): Response<ProfileDetailsRes>


    @FormUrlEncoded
    @POST(SyncConstants.GET_BLOCK_ACCOUNTS_END_POINT)
    suspend fun getBlockAccountDetails(@FieldMap request: HashMap<String, Any>): Response<BlockAccountRes>

    @FormUrlEncoded
    @POST(SyncConstants.CHANGE_PASSWORD_END_POINT)
    suspend fun changePassword(@FieldMap request: HashMap<String, Any>): Response<ChangePasswordRes>

    @FormUrlEncoded
    @POST(SyncConstants.UN_BLOCK_ACCOUNTS_END_POINT)
    suspend fun unBlockAccountDetails(@FieldMap request: HashMap<String, Any>): Response<UnBlockRes>

    @FormUrlEncoded
    @POST(SyncConstants.GET_ALL_AUDIO_END_POINT)
    suspend fun getUploadedSongs(@FieldMap request: HashMap<String, String>): Response<UploadedSongResponse>

    @FormUrlEncoded
    @POST(SyncConstants.GET_ALL_AUDIO_END_POINT)
    suspend fun getAllUploadedSongs(@FieldMap request: HashMap<String, String>): Response<UploadedSongResponse>
  @FormUrlEncoded
    @POST(SyncConstants.GET_AUDIO_UPLOADED_LIMIT)
    suspend fun getUploadedLimitStatus(@FieldMap request: HashMap<String, String>): Response<UploadedLimitStatusResponseModel>

    @FormUrlEncoded
    @POST(SyncConstants.GET_ALL_USER_PROFILE)
    suspend fun getUserListing(@FieldMap request: HashMap<String, Any>): Response<GetUserListingRes>

    @FormUrlEncoded
    @POST(SyncConstants.FAVOURITE_USER)
    suspend fun favouriteUser(@FieldMap request: HashMap<String, Any>): Response<FavouriteUserRes>

    @FormUrlEncoded
    @POST(SyncConstants.FAVOURITE_AUDIO)
    suspend fun favouriteAudio(@FieldMap request: HashMap<String, Any>): Response<FavouriteUserRes>

    @FormUrlEncoded
    @POST(SyncConstants.GET_RECENT_CHAT)
    suspend fun getRecentChat(@FieldMap request: HashMap<String, String>): Response<RecentChatRes>

    @FormUrlEncoded
    @POST(SyncConstants.GET_CHAT_LISTING)
    suspend fun getChatListing(@FieldMap request: HashMap<String, String>): Response<ChatListRes>
    @Multipart
    @POST(SyncConstants.UPLOAD_CHAT_STUFF)
    suspend fun uploadChatFile(
        @Part supportFile: MultipartBody.Part?
    ): Response<UploadChatFileResponseModel>

    @FormUrlEncoded
    @POST(SyncConstants.OTP_VERIFICATION_END_POINT)
    suspend fun otpVerification(@FieldMap request: HashMap<String, String>): Response<OtpVerificationRes>

    @FormUrlEncoded
    @POST(SyncConstants.FORGET_PASSWORD_END_POINT)
    suspend fun forgotPassword(@FieldMap request: HashMap<String, String>): Response<ForgotPasswordRes>


    @FormUrlEncoded
    @POST(SyncConstants.CHANGE_EMAIL)
    suspend fun changeEmail(@FieldMap request: HashMap<String, Any>): Response<ChangePasswordRes>

    @FormUrlEncoded
    @POST(SyncConstants.CHANGE_USERNAME)
    suspend fun changeUserName(@FieldMap request: HashMap<String, Any>): Response<ChangePasswordRes>

    @FormUrlEncoded
    @POST(SyncConstants.ADD_REPORT_AUDIO)
    suspend fun addReportAudio(@FieldMap request: HashMap<String, Any>): Response<CommonResponse>

    @FormUrlEncoded
    @POST(SyncConstants.CHANGE_PHONE_NUMBER)
    suspend fun changePhoneNumber(@FieldMap request: HashMap<String, Any>): Response<ChangePasswordRes>

    @FormUrlEncoded
    @POST(SyncConstants.SIGN_UP_END_POINT)
    suspend fun signup(@FieldMap request: HashMap<String, String>): Response<SignUpResponse>

    @FormUrlEncoded
    @POST(SyncConstants.CHNAGE_NOTIFICATION_SETTING)
    suspend fun changeNotificationStatus(@FieldMap request: HashMap<String, Any>): Response<NotificationResponse>

    @GET(SyncConstants.GET_GOALS)
    suspend fun getGoals(): Response<GetGoalsResponseModel>

    @GET(SyncConstants.GET_ROLE)
    suspend fun getRole(): Response<GetRoleResponseModel>

    @FormUrlEncoded
    @POST(SyncConstants.AUDIO_DESCRIPTION)
    suspend fun getAudioDescription(@FieldMap request: HashMap<String, String>): Response<GetDescriptionResponseModel>
    @FormUrlEncoded
    @POST(SyncConstants.SEARCH)
    suspend fun getSearch(@FieldMap request: HashMap<String, String>): Response<GetSoundFileProfileRes>
 @FormUrlEncoded
    @POST(SyncConstants.REMOVE_MATCHES)
    suspend fun removeMatches(@FieldMap request: HashMap<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST(SyncConstants.NOTIFICATION_LIST)
    suspend fun getNotification(@FieldMap request: HashMap<String, String>): Response<NotificationListResponseModel>

    @Multipart
    @POST(SyncConstants.SUPPORT)
    suspend fun supportUser(
        @PartMap request: HashMap<String, RequestBody>,
        @Part supportFile: MultipartBody.Part?
    ): Response<SupportResponse>

    @Multipart
    @POST(SyncConstants.UPLOAD_PROFILE_IMAGE)
    suspend fun updateUserProfileImage(
        @PartMap request: HashMap<String, RequestBody>,
        @Part supportFile: MultipartBody.Part?
    ): Response<UploadProfileImageRes>

    @Multipart
    @POST(SyncConstants.UPLOAD_AUDIO)
    suspend fun uploadAudio(
        @PartMap request: HashMap<String, RequestBody>,
        @Part fullAudio: MultipartBody.Part?,
        @Part trimAudio: MultipartBody.Part?,
        @Part("roleId[]") arrayListRole: ArrayList<Int>,
        @Part("goalId[]") arrayListGoal: ArrayList<Int>
    ): Response<UploadAudioResponseModel>

    @Multipart
    @POST(SyncConstants.UPLOAD_AUDIO)
    fun uploadAudio_(
        @PartMap request: HashMap<String, RequestBody>,
        @Part fullAudio: MultipartBody.Part?,
        @Part trimAudio: MultipartBody.Part?,
        @Part("roleId[]") arrayListRole: JSONArray,
        @Part("goalId[]") arrayListGoal: JSONArray
    ): Call<ResponseBody>

    @Multipart
    @POST(SyncConstants.UPLOAD_AUDIO)
    fun uploadAudio_Change(
        @PartMap request: HashMap<String, RequestBody>,
        @Part fullAudio: MultipartBody.Part?,
        @Part trimAudio: MultipartBody.Part?,
        @Part mergeVideo: MultipartBody.Part?
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST(SyncConstants.OFFLINE_STATUS)
    fun offlineUser(
        @FieldMap request: HashMap<String, String>
    ): Call<ResponseBody>
}