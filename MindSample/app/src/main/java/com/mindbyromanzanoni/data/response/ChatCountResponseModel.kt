package com.mindbyromanzanoni.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class ChatCountResponseModel {
    @SerializedName("count")
    @Expose
    var count: Int? = null

    @SerializedName("status_code")
    @Expose
    var statusCode: Int? = null

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: Any? = null
}
