package com.in2bliss.data.model

import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse

data class ProfileResponse(
    @SerializedName("profilePicture")
    val profilePicture: String? = null
):BaseResponse()
