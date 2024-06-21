package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 07/03/24
 */
data class UserResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("token")
    val token: String
) : BaseResponse()

data class Data(
    @SerializedName("categories")
    var categories: List<CategoriesData>,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("device_id")
    val deviceId: Any,
    @SerializedName("device_token")
    val deviceToken: Any,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_otp")
    val emailOtp: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_email_verify")
    val isEmailVerify: Int,
    @SerializedName("is_notification")
    val isNotification: Int,
    @SerializedName("is_subscription")
    var isSubscription: String,
    @SerializedName("subscription_plan")
    val subscriptionPlan:String?,
    @SerializedName("subscription_last_date")
    val subscriptionLastData: String?,
    @SerializedName("invite_code")
    val inviteCode:String?,
    @SerializedName("subscription_status")
    val subscriptionStatus: String?,
    @SerializedName("is_user_active")
    val isUserActive: String,
    @SerializedName("language")
    val language: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("max_age")
    val maxAge: Int,
    @SerializedName("min_age")
    val minAge: Int,
    @SerializedName("password")
    val password: String,
    @SerializedName("user_img_path")
    val userImg: String?,
    @SerializedName("user_type")
    val userType: String,
    @SerializedName("notifyReminder")
    val notifyReminder: ReminderData? = null,
    @SerializedName("social_type")
    val socialType: String? = null,
    @SerializedName("social_id")
    val socialId: String? = null
)


data class Choice(
    @SerializedName("choice_img")
    val choiceImg: String,
    @SerializedName("choice_name")
    val choiceName: String,
    @SerializedName("id")
    val id: Int
)

data class ReminderData(
    @SerializedName("days")
    val days: List<String>,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("times_per_day")
    val timesPerDay: Int,
    @SerializedName("user_id")
    val userId: Int
)