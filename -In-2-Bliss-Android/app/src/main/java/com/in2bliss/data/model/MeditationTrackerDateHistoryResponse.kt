package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName

data class MeditationTrackerDateHistoryResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("createdBy")
        val createdBy: Int?,
        @SerializedName("date")
        var date: String?,
        @SerializedName("endTime")
        val endTime: Int?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("thumbnail")
        val image: String?,
        @SerializedName("title")
        val title: String?
    )
}