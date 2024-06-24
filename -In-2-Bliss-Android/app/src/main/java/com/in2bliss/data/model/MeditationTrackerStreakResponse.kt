package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName

data class MeditationTrackerStreakResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("graphData")
    val graphData: ArrayList<GraphData>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("totalEntries")
        val totalEntries: Int?=0,
        @SerializedName("totalMeditationTime")
        val totalMeditationTime: Int?=0,
        @SerializedName("weekStreak")
        val weekStreak: String?=null
    )

    data class GraphData(
        @SerializedName("date")
        val date: String?,
        @SerializedName("day")
        val day: String?,
        @SerializedName("minutes")
        var minutes: Int?
    )
}