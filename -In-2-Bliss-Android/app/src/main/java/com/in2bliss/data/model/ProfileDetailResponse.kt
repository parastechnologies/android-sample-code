package com.in2bliss.data.model

data class ProfileDetailResponse(
    val `data`: Data?,
    val message: String?,
    val status: Int?
) {
    data class Data(
        val RID: Any?,
        val activeSubscription: Int?,
        val address: Any?,
        val country: Any?,
        val created_at: String?,
        val deviceToken: String?,
        val deviceType: String?,
        val email: String?,
        val fullName: String?,
        val gender: Any?,
        val id: Int?,
        val isCancel: Any?,
        val isLogin: Int?,
        val journalStatus: Int?,
        val loginType: String?,
        val phoneNumber: Any?,
        val profilePicture: String?,
        val profileStatus: Int?,
        val quoteStatus: Int?,
        val sleepStatus: Int?,
        val socialID: Any?,
        val status: Int?,
        val subscriptionStatus: Int?,
        val updated_at: String?
    )
}