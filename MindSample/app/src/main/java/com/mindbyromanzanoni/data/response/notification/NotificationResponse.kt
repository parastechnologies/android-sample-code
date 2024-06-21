package com.mindbyromanzanoni.data.response.notification

import com.mindbyromanzanoni.base.BaseResponse

data class NotificationResponse(
    val `data`: NotificationDataResponse
) : BaseResponse()

data class NotificationDataResponse(
    val notificationList : ArrayList<NotificationListResponse>? = arrayListOf(),
    val remiderDetail: ReminderData
)

data class ReminderData(
    var reminderCustomDate :String?,
    var reminderTypeId :Int?,
)

data class NotificationListResponse(
    val body: String,
    val notificatioOn: String,
    val notificationId: Int,
    val notificationTypeId: Int,
    val sentToId: Int,
    val title: String
)
