package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName

data class HomeGuidedMeditation(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("curated")
        val curated: List<Popular?>?,
        @SerializedName("popular")
        val popular: List<Popular?>?
    ) {
        data class Curated(
            @SerializedName("audio")
            val audio: String?,
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
            @SerializedName("description")
            val description: String?,
            @SerializedName("duration")
            val duration: Double?,
            @SerializedName("favouriteStatus")
            val favouriteStatus: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("liveStatus")
            val liveStatus: Int?,
            @SerializedName("SCID")
            val sCID: Int?,
            @SerializedName("scheduleTime")
            val scheduleTime: String?,
            @SerializedName("status")
            val status: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("type")
            val type: Int?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("views")
            val views: Int?
        )

        data class Popular(
            @SerializedName("audio")
            val audio: String?,
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
            @SerializedName("description")
            val description: String?,
            @SerializedName("duration")
            val duration: Double?,
            @SerializedName("favouriteStatus")
            val favouriteStatus: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("liveStatus")
            val liveStatus: Int?,
            @SerializedName("SCID")
            val sCID: Int?,
            @SerializedName("scheduleTime")
            val scheduleTime: String?,
            @SerializedName("status")
            val status: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("type")
            val type: Int?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("views")
            val views: Int?
        )
    }
}