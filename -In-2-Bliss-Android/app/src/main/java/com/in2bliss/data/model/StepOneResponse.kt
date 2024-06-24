package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse

data class StepOneResponse(
    @SerializedName("data")
    val `data`: List<Data>
) : BaseResponse() {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("reason")
        val reason: String?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        var isSelected : Boolean? = false
    )
}