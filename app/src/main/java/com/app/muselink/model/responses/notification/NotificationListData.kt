package com.app.muselink.model.responses.notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationListData {
    @SerializedName("Notification_Id")
    @Expose
    var notificationId: String? = null

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("Notification_Date")
    @Expose
    var notificationDate: String? = null

    @SerializedName("User_Name")
    @Expose
    var userName: Any? = null

    @SerializedName("Profile_Image")
    @Expose
    var profileImage: Any? = null

    @SerializedName("id")
    @Expose
    var id: String? = null
}