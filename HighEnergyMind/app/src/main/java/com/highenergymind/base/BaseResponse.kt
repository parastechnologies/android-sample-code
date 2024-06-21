package com.highenergymind.base

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("isAuthorized")
    val code: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("success")
    val success: Boolean? = null
)