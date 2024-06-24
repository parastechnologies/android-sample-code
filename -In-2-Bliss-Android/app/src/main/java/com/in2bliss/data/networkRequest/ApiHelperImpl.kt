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
import com.in2bliss.domain.ApiHelperInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ApiHelperImpl(private val apiService: ApiService) : ApiHelperInterface {

    override suspend fun signUpReasons(): Response<StepOneResponse> {
        return apiService.signupReasons()
    }

    override suspend fun meditationStreak(): Response<MeditationTrackerStreakResponse> {
        return apiService.meditationStreak()
    }

    override suspend fun signUp(body: HashMap<String, String>): Response<LogInResponse> {
        return apiService.signUp(
            body = body
        )
    }

    override suspend fun favourite(body: HashMap<String, String>): Response<FavouritesResponse> {
        return apiService.favourites(
            body = body
        )
    }

    override suspend fun adminAffirmation(body: HashMap<String, String>): Response<AdminAffirmationResponse> {
        return apiService.adminAffirmation(
            body=body
        )
    }

    override suspend fun shareUrl(body: HashMap<String, String>): Response<ShareResponse> {
        return apiService.shareUrl(
            body=body
        )
    }


    override suspend fun meditationTrackerHistory(body: HashMap<String, String>): Response<MeditationTrackerHistoryResponse> {
        return apiService.meditationTrackerHistory(
            body = body
        )
    }

    override suspend fun meditationTrackerDate(body: HashMap<String, String>): Response<MeditationTrackerDateHistoryResponse> {
        return apiService.meditationTrackerDateHistory(
            body = body
        )
    }

    override suspend fun meditationTrackerDelete(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.meditationTrackerDeleteHistory(
            body = body
        )
    }


    override suspend fun addGuidedJournal(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.addGuidedJournal(
            body = body
        )
    }

    override suspend fun logIn(body: HashMap<String, String>): Response<LogInResponse> {
        return apiService.logIn(
            body = body
        )
    }

    override suspend fun homeGuidedMeditation(body: HashMap<String, String>): Response<MusicList> {
        return apiService.homeGuidedMeditation(
            body = body
        )
    }

    override suspend fun homeGuidedSleep(body: HashMap<String, String>): Response<MusicList> {
        return apiService.homeGuidedSleep(
            body = body
        )
    }

    override suspend fun homeGuidedAffirmation(body: HashMap<String, String>): Response<MusicList> {
        return apiService.homeGuidedAffirmation(
            body = body
        )
    }

    override suspend fun homeGuidedWisdom(body: HashMap<String, String>): Response<MusicList> {
        return apiService.homeGuidedWisdom(
            body = body
        )
    }

    override suspend fun myAffirmation(body: HashMap<String, String>): Response<MusicList> {
        return apiService.myAffirmation(
            body = body
        )
    }

    override suspend fun getProfileDetail(): Response<ProfileDetailResponse> {
        return apiService.getProfileDetail()
    }

    override suspend fun getNotificationStatus(): Response<GetNotificationStatusResponse> {
        return apiService.getNotificationStatus()
    }

    override suspend fun setNotificationStatus(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.setNotificationStatus(
            body = body
        )
    }

    override suspend fun getExplore(body: HashMap<String, String>): Response<MusicList> {
        return apiService.getExplore(
            body = body
        )
    }

    override suspend fun socialLogIn(body: HashMap<String, String>): Response<LogInResponse> {
        return apiService.socialLogIn(
            body = body
        )
    }

    override suspend fun addName(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.addName(
            body = body
        )
    }

    override suspend fun selfMeditationSessionEnd(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.selfMeditationSessionEnd(
            body = body
        )
    }

    override suspend fun addProfilePicture(imageFile: MultipartBody.Part): Response<ProfileResponse> {
        return apiService.addProfilePicture(
            imageFile = imageFile
        )
    }

    override suspend fun categoryList(body: HashMap<String, String>): Response<CategoryResponse> {
        return apiService.categoryList(body = body)
    }

    override suspend fun getSharedData(body: HashMap<String, String>): Response<GetShareDataResponse> {
        return apiService.getSharedData(body = body)
    }

    override suspend fun getHomeData(): Response<HomeResponse> {
       return apiService.getHomeData()
    }

    override suspend fun readNotification(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.readNotification(body = body)
    }

    override suspend fun addSubCategory(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.addSubCategory(
            body = body
        )
    }

    override suspend fun setReminder(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.setReminder(
            body = body
        )
    }

    override suspend fun setJournalReminder(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.setJournalReminder(
            body = body
        )
    }

    override suspend fun updateReminder(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.updateReminder(
            body = body
        )
    }

    override suspend fun addTextAffirmation(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.addTextAffirmation(
            body = body
        )
    }

    override suspend fun updateTextAffirmation(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.updateTextAffirmation(
            body = body
        )
    }

    override suspend fun textAffirmationList(body: HashMap<String, String>): Response<AffirmationListResponse> {
        return apiService.textAffirmationList(
            body = body
        )
    }
    override suspend fun myTextAffirmationList(body: HashMap<String, String>): Response<TextAffirmationListResponse> {
        return apiService.myTextAffirmationList(
            body = body
        )
    }

    override suspend fun quotesList(body: HashMap<String, String>): Response<QuotesResponseModel> {
        return apiService.quotesList(body = body)
    }

    override suspend fun guidedJournal(body: HashMap<String, String>): Response<GuidedJournalListResponse> {
        return apiService.guidedJournal(
            body = body
        )
    }


    override suspend fun upLoadData(
        multipartBody: MultipartBody.Part,
        body: HashMap<String, RequestBody>
    ): Response<Upload> {
        return apiService.uploadData(
            imageFile = multipartBody,
            body = body
        )
    }

    override suspend fun editProfile(
        multipartBody: MultipartBody.Part,
        body: HashMap<String, RequestBody>
    ): Response<EditProfileResponse> {
        return apiService.editProfile(
            imageFile = multipartBody,
            body = body
        )
    }

    override suspend fun deleteAffirmation(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.deleteAffirmation(
            body = body
        )
    }

    override suspend fun favouriteAffirmation(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.favouriteAffirmation(
            body = body
        )
    }

    override suspend fun mediationSeeALl(body: HashMap<String, String>): Response<SeeAllResponse> {
        return apiService.meditationSeeAll(body = body)
    }

    override suspend fun myAffirmationSeeAll(body: HashMap<String, String>): Response<SeeAllResponse> {
        return apiService.myAffirmationSeeAll(body = body)
    }

    override suspend fun wisdomSeeAll(body: HashMap<String, String>): Response<SeeAllResponse> {
        return apiService.wisdomSeeAll(body = body)
    }

    override suspend fun guidedAffirmationSeeALl(body: HashMap<String, String>): Response<SeeAllResponse> {
        return apiService.guidedAffirmationSeeAll(body = body)
    }

    override suspend fun exploreSeeALl(body: HashMap<String, String>): Response<SeeAllResponse> {
        return apiService.exploreSeeALl(body = body)
    }

    override suspend fun sleepSeeALl(body: HashMap<String, String>): Response<SeeAllResponse> {
        return apiService.sleepSeeALl(body = body)
    }

    override suspend fun getNotificationList(body: HashMap<String, String>): Response<NotificationListResponse> {
        return apiService.getNotificationList(body = body)
    }

    override suspend fun guidedMeditationSessionEnd(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.guidedMeditationSessionEnd(
            body = body
        )
    }

    override suspend fun guidedAffirmationSessionEnd(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.guidedAffirmationSessionEnd(
            body = body
        )
    }

    override suspend fun guidedWisdomSessionEnd(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.guidedWisdomSessionEnd(
            body = body
        )
    }

    override suspend fun chooseJournalBackground(body: HashMap<String, String>): Response<ChooseBackgroundResponse> {
        return apiService.chooseJournalBackground(body)
    }

    override suspend fun chooseAffirmationBackground(body: HashMap<String, String>): Response<ChooseBackgroundResponse> {
        return apiService.chooseAffirmationBackground(body)
    }




    override suspend fun logout(): Response<BaseResponse> {
        return apiService.logout()
    }

    override suspend fun deleteAccount(): Response<BaseResponse> {
        return apiService.accountDelete()
    }

    override suspend fun deleteJournal(
        body: HashMap<String, String>
    ): Response<BaseResponse> {
        return apiService.deleteJournal(
            body = body
        )
    }

    override suspend fun journalStreak(body: HashMap<String, String>): Response<JournalStreak> {
        return apiService.journalStreak(
            body = body
        )
    }

    override suspend fun editJournal(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.editJournal(
            body = body
        )
    }

    override suspend fun getMusicCategories(): Response<MusicCategories> {
        return apiService.getMusicCategories()
    }

    override suspend fun getMusicList(body: HashMap<String, String>): Response<com.in2bliss.data.model.musicCateogries.MusicList> {
        return apiService.getMusicList(
            body = body
        )
    }

    override suspend fun customizeAffirmation(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.customizeAffirmation(
            body = body
        )
    }

   override suspend fun contactUs(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.contactUs(
            body = body
        )
    }

    override suspend fun addSpokenAffirmation(
        body: HashMap<String, String>
    ): Response<BaseResponse> {
        return apiService.addSpokenAffirmation(
            body = body,
        )
    }

    override suspend fun updateSpokenAffirmation(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.updateSpokenAffirmation(
            body = body,
        )
    }

    override suspend fun subscribe(body: HashMap<String, String>): Response<BaseResponse> {
        return apiService.subscribe(
            hashMap = body
        )
    }

    override suspend fun getSubscriptionStatus(): Response<Subscription> {
        return apiService.getSubscriptionStatus()
    }
}

