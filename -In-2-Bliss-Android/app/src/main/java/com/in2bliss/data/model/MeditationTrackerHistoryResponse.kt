package com.in2bliss.data.model

import com.google.gson.annotations.SerializedName

data class MeditationTrackerHistoryResponse(
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
        val date: String?,
        @SerializedName("endTime")
        val endTime: Int?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("title")
        val title: String?
    )
}