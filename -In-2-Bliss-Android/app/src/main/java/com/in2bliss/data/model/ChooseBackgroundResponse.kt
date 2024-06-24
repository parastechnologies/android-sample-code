package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName

data class ChooseBackgroundResponse(
    @SerializedName("data")
    val `data`: List<Data>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("background")
        val background: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("type")
        val type: Int?,
        @SerializedName("updated_at")
        val updatedAt: Any?
    )
}