package com.in2bliss.data.model


import android.os.Parcelable
import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse
import kotlinx.parcelize.Parcelize

data class LogInResponse(
    @SerializedName("data")
    val `data`: Data,
    /**
     * This token key used in case of SignUp
     */
    @SerializedName("token")
    val token: String? = null
) : BaseResponse() {
    data class Data(
        @SerializedName("activeSubscription")
        val activeSubscription: Int,
        var quotesReminderSkip: Int? = 0,
        var journalReminderSkip: Int? = 0,
        @SerializedName("address")
        val address: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("deviceToken")
        val deviceToken: String?,
        @SerializedName("deviceType")
        val deviceType: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("fullName")
        var fullName: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("id")
        val id: Int? = -1,
        @SerializedName("isLogin")
        val isLogin: Int? = -1,
        @SerializedName("loginType")
        val loginType: String?,
        @SerializedName("phoneNumber")
        val phoneNumber: String?,
        @SerializedName("profilePicture")
        var profilePicture: String?,
        @SerializedName("profileStatus")
        var profileStatus: Int? = -1,
        @SerializedName("RID")
        val rID: Int? = -1,
        @SerializedName("quoteStatus")
        var quoteStatus: Int?,
        @SerializedName("socialID")
        val socialID: String?,
        @SerializedName("status")
        val status: Int? = -1,
        @SerializedName("subscriptionStatus")
        val subscriptionStatus: Int? = -1,
        var journalStatus: Int?=0,
        var sleepStatus: Int?,
        var isFirstTime: Int = 0,

        /**
         * For logIn & socialLogIn we'll get the token inside the data
         */
        @SerializedName("token")
        var token: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        var welcomeScreen: WelcomeScreen?,

        /** For subscription api not hit */
        var purchaseToken: String?,
        var planType: String?,
        var isSubscriptionApiHit: Boolean? = false
    ) {

        @Keep
        @Parcelize
        data class WelcomeScreen(
            var guidedAffirmation: Boolean = false,
            var guidedMeditation: Boolean = false,
            var createAffirmation: Boolean = false,
            var gratitudeJournal: Boolean = false
        ) : Parcelable
    }
}