package com.app.muselink.retrofit

import com.app.muselink.util.performGetOperation
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepository @Inject constructor(
    private val remoteDataSource: ApiRemoteDataSource
) {

    fun loginUser(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.loginUser(request) }
    )

    fun subscription(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.subscription(request) }
    )

    fun subscriptionStatus(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.subscriptionStatus(request) }
    )

    fun logoutUser(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.logoutUser(request) }
    )

    fun getSoundFiles(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getSoundFiles(request) }
    )

    fun deleteAudio(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.deleteAudio(request) }
    )

    fun chnageNotificationSatusAudio(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.chnageNotificationSatusAudio(request) }
    )

    fun getInterests(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getInterests(request) }
    )

    fun getEditInterests(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getEditInterests(request) }
    )

    fun getGoals(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getGoals(request) }
    )

    fun updateInterest(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.updateInterest(request) }
    )

    fun updateBiography(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.updateBiography(request) }
    )

    fun updatepersonalCareerGoals(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.updatepersonalCareerGoals(request) }
    )

    fun getpersonalInterests(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getpersonalInterests(request) }
    )

    fun deleteAccount(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.deleteAccount(request) }
    )

    fun unBlockAccountDetails(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.unBlockAccountDetails(request) }
    )

    fun getBlockAccountDetails(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getBlockAccountDetails(request) }
    )

    fun changePassword(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.changePassword(request) }
    )

    fun getProfileDetails(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getProfileDetails(request) }
    )

    fun setAccountStatus(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.setAccountStatus(request) }
    )

    fun getUploadedSongs(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getUploadedSongs(request) }
    )


    fun getAllUploadedSongs(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getAllUploadedSongs(request) }
    )

    fun getUploadLimitStatus(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getUploadedLimitStatus(request) }
    )

    fun otpVerification(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.otpVerification(request) }
    )

    fun forgotPassword(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.forgotPassword(request) }
    )

    fun changeEmail(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.changeEmail(request) }
    )
    fun search(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.search(request) }
    )

    fun removeMatches(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.removeMatches(request) }
    )
    fun description(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.audioDescription(request) }
    )
    fun notificationList(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getNotification(request) }
    )

    fun changeUserName(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.changeUserName(request) }
    )

    fun addReportAudio(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.addReportAudio(request) }
    )

    fun changePhoneNumber(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.changePhoneNumber(request) }
    )

    fun signUpUser(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.signupUser(request) }
    )

    fun getGoals() = performGetOperation(
        networkCall = { remoteDataSource.getGoals() }
    )

    fun getRole() = performGetOperation(
        networkCall = { remoteDataSource.getRole() }
    )
//    SupportType="ReportAProblem",UserId,Review,


    fun changeNotificationStatus(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.changeNotificationStatus(request) }
    )

    fun supportUser(request: HashMap<String, RequestBody>, SupportFile: MultipartBody.Part) =
        performGetOperation(
            networkCall = { remoteDataSource.support(request, SupportFile) }
        )

    fun updateUserProfileImage(request: HashMap<String, RequestBody>, SupportFile: MultipartBody.Part) =
        performGetOperation(
            networkCall = { remoteDataSource.updateUserProfileImage(request, SupportFile) }
        )

    fun getUserListing(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.getUserListing(request) }
    )

    fun favouriteUser(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.favouriteUser(request) }
    )

    fun favouriteAudio(request: HashMap<String, Any>) = performGetOperation(
        networkCall = { remoteDataSource.favouriteAudio(request) }
    )


    fun getRecentChat(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getRecentChat(request) }
    )

    fun getChatListing(request: HashMap<String, String>) = performGetOperation(
        networkCall = { remoteDataSource.getChatListing(request) }
    )

    fun uploadChatFile(request: MultipartBody.Part) = performGetOperation(
        networkCall = { remoteDataSource.uploadChatFile(request) }
    )

    fun uploadAudio(
        requestMap: HashMap<String, RequestBody>,
        fullAudio: MultipartBody.Part,
        trimAudio: MultipartBody.Part,
        arrayListRole: ArrayList<Int>,
        arrayListGoal: ArrayList<Int>
    ) = performGetOperation(
        networkCall = {
            remoteDataSource.uploadAudio(
                requestMap,
                fullAudio,
                trimAudio,
                arrayListRole,
                arrayListGoal
            )
        })
}