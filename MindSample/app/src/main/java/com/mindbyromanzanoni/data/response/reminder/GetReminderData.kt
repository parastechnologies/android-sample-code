package com.mindbyromanzanoni.data.response.reminder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetReminderData {
    @SerializedName("reminderId")
    @Expose
    var reminderId: Int? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("reminderDate")
    @Expose
    var reminderDate: String? = null

    @SerializedName("reminderTypeId")
    @Expose
    var reminderTypeId: Any? = null

    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("reminderTypeIds")
    @Expose
    var reminderTypeIds: String? = null

    @SerializedName("user")
    @Expose
    var user: Any? = null
}
