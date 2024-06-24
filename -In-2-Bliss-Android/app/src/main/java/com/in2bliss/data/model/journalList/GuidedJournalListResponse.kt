package com.in2bliss.data.model.journalList


import com.google.gson.annotations.SerializedName

data class GuidedJournalListResponse(
    @SerializedName("data")
    val `data`: List<Data>,
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
        @SerializedName("date")
        val date: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("title")
        var title: String?,
        @SerializedName("UID")
        val uID: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?
    )
}