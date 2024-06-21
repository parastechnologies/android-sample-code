package com.highenergymind.data
import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 30/04/24
 */
data class NotificationResponse(
    @SerializedName("data")
    val `data`: List<NotData>

):BaseResponse()

data class NotData(
    @SerializedName("aff_id")
    val affId: Int,
    @SerializedName("category_img_path")
    val categoryImgPath: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("notification_msg")
    val notificationMsg: String,
    @SerializedName("notification_read_status")
    val notificationReadStatus: String,
    @SerializedName("notification_title")
    val notificationTitle: String,
    @SerializedName("notification_type")
    val notificationType: String,
    @SerializedName("receiver_id")
    val receiverId: Int,
    @SerializedName("sender_id")
    val senderId: Int
)