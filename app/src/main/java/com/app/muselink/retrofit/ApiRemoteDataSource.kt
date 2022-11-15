package com.app.muselink.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class ApiRemoteDataSource @Inject constructor(
    private val apiServices: ApiServices
) : BaseDataSource() {

    suspend fun loginUser(request: HashMap<String, String>) = getResult { apiServices.login(request) }

    suspend fun subscription(request: HashMap<String, String>) = getResult { apiServices.subscription(request) }

    suspend fun subscriptionStatus(request: HashMap<String, String>) = getResult { apiServices.subscriptionStatus(request) }

    suspend fun logoutUser(request: HashMap<String, Any>) =
        getResult { apiServices.logoutUser(request) }

    suspend fun getSoundFiles(request: HashMap<String, String>) =
        getResult { apiServices.getSoundFiles(request) }


    suspend fun deleteAudio(request: HashMap<String, String>) =
        getResult { apiServices.deleteAudio(request) }


    suspend fun chnageNotificationSatusAudio(request: HashMap<String, String>) =
        getResult { apiServices.chnageNotificationSatusAudio(request) }

    suspend fun getpersonalInterests(request: HashMap<String, Any>) =
        getResult { apiServices.getpersonalInterests(request) }

    suspend fun getInterests(request: HashMap<String, Any>) =
        getResult { apiServices.getInterests(request) }

    suspend fun getEditInterests(request: HashMap<String, Any>) =
        getResult { apiServices.getEditInterests(request) }

    suspend fun getGoals(request: HashMap<String, Any>) =
        getResult { apiServices.getGoals(request) }

    suspend fun updateInterest(request: HashMap<String, Any>) =
        getResult { apiServices.updatepersonalInterests(request) }

    suspend fun updatepersonalCareerGoals(request: HashMap<String, Any>) =
        getResult { apiServices.updatepersonalCareerGoals(request) }

    suspend fun updateBiography(request: HashMap<String, Any>) =
        getResult { apiServices.updateBiography(request) }

    suspend fun deleteAccount(request: HashMap<String, Any>) =
        getResult { apiServices.deleteAccount(request) }

    suspend fun getProfileDetails(request: HashMap<String, Any>) =
        getResult { apiServices.getProfileDetails(request) }

    suspend fun setAccountStatus(request: HashMap<String, Any>) =
        getResult { apiServices.setAccountStatus(request) }

    suspend fun getBlockAccountDetails(request: HashMap<String, Any>) =
        getResult { apiServices.getBlockAccountDetails(request) }

    suspend fun changePassword(request: HashMap<String, Any>) =
        getResult { apiServices.changePassword(request) }

    suspend fun unBlockAccountDetails(request: HashMap<String, Any>) =
        getResult { apiServices.unBlockAccountDetails(request) }

    suspend fun getUploadedSongs(request: HashMap<String, String>) =
        getResult { apiServices.getUploadedSongs(request) }


    suspend fun getAllUploadedSongs(request: HashMap<String, String>) =
        getResult { apiServices.getAllUploadedSongs(request) }

 suspend fun getUploadedLimitStatus(request: HashMap<String, String>) =
        getResult { apiServices.getUploadedLimitStatus(request) }

    suspend fun getUserListing(request: HashMap<String, Any>) =
        getResult { apiServices.getUserListing(request) }

    suspend fun favouriteUser(request: HashMap<String, Any>) =
        getResult { apiServices.favouriteUser(request) }

    suspend fun favouriteAudio(request: HashMap<String, Any>) =
        getResult { apiServices.favouriteAudio(request) }

    suspend fun getRecentChat(request: HashMap<String, String>) =
        getResult { apiServices.getRecentChat(request) }

    suspend fun getChatListing(request: HashMap<String, String>) =
        getResult { apiServices.getChatListing(request) }

    suspend fun uploadChatFile(request: MultipartBody.Part) =
        getResult { apiServices.uploadChatFile(request) }

    suspend fun otpVerification(request: HashMap<String, String>) =
        getResult { apiServices.otpVerification(request) }

    suspend fun forgotPassword(request: HashMap<String, String>) =
        getResult { apiServices.forgotPassword(request) }


    suspend fun changeEmail(request: HashMap<String, Any>) =
        getResult { apiServices.changeEmail(request) }

    suspend fun audioDescription(request: HashMap<String, String>) =
        getResult { apiServices.getAudioDescription(request) }

    suspend fun search(request: HashMap<String, String>) =
        getResult { apiServices.getSearch(request) }
 suspend fun removeMatches(request: HashMap<String, String>) =
        getResult { apiServices.removeMatches(request) }

    suspend fun getNotification(request: HashMap<String, String>) =
        getResult { apiServices.getNotification(request) }


    suspend fun changeUserName(request: HashMap<String, Any>) =
        getResult { apiServices.changeUserName(request) }

    suspend fun addReportAudio(request: HashMap<String, Any>) =
        getResult { apiServices.addReportAudio(request) }

    suspend fun changePhoneNumber(request: HashMap<String, Any>) =
        getResult { apiServices.changePhoneNumber(request) }


    suspend fun signupUser(request: HashMap<String, String>) =
        getResult { apiServices.signup(request) }

    suspend fun changeNotificationStatus(request: HashMap<String, Any>) =
        getResult { apiServices.changeNotificationStatus(request) }

    suspend fun getGoals() = getResult { apiServices.getGoals() }
    suspend fun getRole() = getResult { apiServices.getRole() }


    suspend fun support(request: HashMap<String, RequestBody>, SupportFile: MultipartBody.Part) =
        getResult { apiServices.supportUser(request, SupportFile) }

    suspend fun updateUserProfileImage(
        request: HashMap<String, RequestBody>,
        SupportFile: MultipartBody.Part
    ) = getResult { apiServices.updateUserProfileImage(request, SupportFile) }


    suspend fun uploadAudio(
        requestMap: HashMap<String, RequestBody>,
        fullAudio: MultipartBody.Part,
        trimAudio: MultipartBody.Part,
        arrayListRole: ArrayList<Int>,
        arrayListGoal: ArrayList<Int>
    ) = getResult {
        apiServices.uploadAudio(requestMap, fullAudio, trimAudio, arrayListRole, arrayListGoal)
    }

}