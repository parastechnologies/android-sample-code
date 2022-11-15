package com.app.muselink.data.modals.responses.getDescription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDescriptionResponseModel {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: GetDescriptionData? = null
}