package com.app.muselink.data.modals.responses.notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.app.muselink.model.responses.notification.NotificationListData

class NotificationListResponseModel {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<NotificationListData>? = null
}