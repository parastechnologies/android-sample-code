package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName

data class ExploreResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("data")
        val `data`: List<Data?>?,
        @SerializedName("title")
        val title: String?
    ) {
        data class Data(
            @SerializedName("affirmation")
            val affirmation: Any?,
            @SerializedName("audio")
            val audio: Any?,
            @SerializedName("audioName")
            val audioName: Any?,
            @SerializedName("audioType")
            val audioType: Any?,
            @SerializedName("background")
            val background: String?,
            @SerializedName("CID")
            val cID: Int?,
            @SerializedName("categoryIcon")
            val categoryIcon: String?,
            @SerializedName("categoryName")
            val categoryName: String?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("createdBy")
            val createdBy: Int?,
            @SerializedName("description")
            val description: String?,
            @SerializedName("duration")
            val duration: Int?,
            @SerializedName("favouriteStatus")
            val favouriteStatus: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("introAffirmation")
            val introAffirmation: Any?,
            @SerializedName("liveStatus")
            val liveStatus: Int?,
            @SerializedName("SCID")
            val sCID: Int?,
            @SerializedName("scheduleTime")
            val scheduleTime: Any?,
            @SerializedName("status")
            val status: Int?,
            @SerializedName("thumbnail")
            val thumbnail: Any?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("transcript")
            val transcript: Any?,
            @SerializedName("type")
            val type: Int?,
            @SerializedName("UID")
            val uID: Int?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )
    }
}