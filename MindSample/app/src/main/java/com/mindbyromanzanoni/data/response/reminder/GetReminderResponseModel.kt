package com.mindbyromanzanoni.data.response.reminder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindbyromanzanoni.base.BaseResponse

class GetReminderResponseModel : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: List<GetReminderData>? = null
}
