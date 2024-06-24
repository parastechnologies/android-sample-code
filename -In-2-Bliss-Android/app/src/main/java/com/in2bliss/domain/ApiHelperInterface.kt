package com.in2bliss.domain

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

interface ApiHelperInterface {

    suspend fun signUpReasons(): Response<StepOneResponse>

    suspend fun meditationStreak(): Response<MeditationTrackerStreakResponse>

    suspend fun signUp(body: HashMap<String, String>): Response<LogInResponse>

    suspend fun favourite(body: HashMap<String, String>): Response<FavouritesResponse>

    suspend fun adminAffirmation(body: HashMap<String, String>): Response<AdminAffirmationResponse>

    suspend fun shareUrl(body: HashMap<String, String>): Response<ShareResponse>

    suspend fun meditationTrackerHistory(body: HashMap<String, String>): Response<MeditationTrackerHistoryResponse>

    suspend fun meditationTrackerDate(body: HashMap<String, String>): Response<MeditationTrackerDateHistoryResponse>

    suspend fun meditationTrackerDelete(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun addGuidedJournal(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun logIn(body: HashMap<String, String>): Response<LogInResponse>

    suspend fun homeGuidedMeditation(body: HashMap<String, String>): Response<MusicList>
    suspend fun homeGuidedSleep(body: HashMap<String, String>): Response<MusicList>

    suspend fun homeGuidedAffirmation(body: HashMap<String, String>): Response<MusicList>

    suspend fun homeGuidedWisdom(body: HashMap<String, String>): Response<MusicList>

    suspend fun myAffirmation(body: HashMap<String, String>): Response<MusicList>

    suspend fun getProfileDetail(): Response<ProfileDetailResponse>

    suspend fun getNotificationStatus(): Response<GetNotificationStatusResponse>

    suspend fun setNotificationStatus(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun getExplore(body: HashMap<String, String>): Response<MusicList>

    suspend fun socialLogIn(body: HashMap<String, String>): Response<LogInResponse>

    suspend fun addName(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun selfMeditationSessionEnd(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun addProfilePicture(imageFile: MultipartBody.Part): Response<ProfileResponse>

    suspend fun categoryList(body: HashMap<String, String>): Response<CategoryResponse>

    suspend fun getSharedData(body: HashMap<String, String>): Response<GetShareDataResponse>

    suspend fun getHomeData(): Response<HomeResponse>

    suspend fun readNotification(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun addSubCategory(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun setReminder(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun setJournalReminder(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun updateReminder(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun addTextAffirmation(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun updateTextAffirmation(body: HashMap<String, String>): Response<BaseResponse>

    suspend fun textAffirmationList(body: HashMap<String, String>): Response<AffirmationListResponse>
    suspend fun myTextAffirmationList(body: HashMap<String, String>): Response<TextAffirmationListResponse>


    suspend fun quotesList(body: HashMap<String, String>): Response<QuotesResponseModel>

    suspend fun guidedJournal(body: HashMap<String, String>): Response<GuidedJournalListResponse>


    suspend fun upLoadData(
        multipartBody: MultipartBody.Part,
        body: HashMap<String, RequestBody>
    ): Response<Upload>

    suspend fun editProfile(
        multipartBody: MultipartBody.Part,
        body: HashMap<String, RequestBody>
    ): Response<EditProfileResponse>

    suspend fun deleteAffirmation(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun favouriteAffirmation(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun mediationSeeALl(
        body: HashMap<String, String>
    ): Response<SeeAllResponse>

    suspend fun myAffirmationSeeAll(
        body: HashMap<String, String>
    ): Response<SeeAllResponse>

    suspend fun wisdomSeeAll(
        body: HashMap<String, String>
    ): Response<SeeAllResponse>

    suspend fun guidedAffirmationSeeALl(
        body: HashMap<String, String>
    ): Response<SeeAllResponse>

    suspend fun exploreSeeALl(
        body: HashMap<String, String>
    ): Response<SeeAllResponse>

    suspend fun sleepSeeALl(
        body: HashMap<String, String>
    ): Response<SeeAllResponse>


    suspend fun getNotificationList(
        body: HashMap<String, String>
    ): Response<NotificationListResponse>

    suspend fun guidedMeditationSessionEnd(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun guidedAffirmationSessionEnd(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun guidedWisdomSessionEnd(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun chooseJournalBackground(body: HashMap<String, String>): Response<ChooseBackgroundResponse>
    suspend fun chooseAffirmationBackground(body: HashMap<String, String>): Response<ChooseBackgroundResponse>


    suspend fun logout(): Response<BaseResponse>
    suspend fun deleteAccount(): Response<BaseResponse>

    suspend fun deleteJournal(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun journalStreak(
        body: HashMap<String, String>
    ): Response<JournalStreak>

    suspend fun editJournal(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun getMusicCategories(): Response<MusicCategories>

    suspend fun getMusicList(
        body: HashMap<String, String>
    ): Response<com.in2bliss.data.model.musicCateogries.MusicList>

    suspend fun customizeAffirmation(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun contactUs(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun addSpokenAffirmation(
        body: HashMap<String, String>,
    ): Response<BaseResponse>

    suspend fun updateSpokenAffirmation(
        body: HashMap<String, String>,
    ): Response<BaseResponse>

    suspend fun subscribe(
        body: HashMap<String, String>
    ): Response<BaseResponse>

    suspend fun getSubscriptionStatus() : Response<Subscription>
}

