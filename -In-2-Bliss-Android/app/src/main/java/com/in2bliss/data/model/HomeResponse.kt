package com.in2bliss.data.model

import com.google.gson.annotations.SerializedName
import com.in2bliss.data.model.musicList.MusicList

data class HomeResponse(
    val `data`: List<Data?>?,
    var textAffirmation: TextAffirmation? = null,
    val message: String?,
    val status: Int?
) {
    data class Data(
        val `data`: List<Data?>?,
        val title: String?,
        val type: Int?,

        ) {


        data class Data(
            val CID: Int?,
            val SCID: Int?,
            val UID: Int?,
            val affirmation: String?,
            val audio: String?,
            val audioName: String?,
            val audioType: Any?,
            val background: String?,
            val category: Category?,
            val categoryIcon: String?,
            val categoryName: String?,
            val createdBy: Int?,
            val created_at: String?,
            val customise: MusicList.Customise?,
            val dataType: Int?,
            val description: String?,
            val duration: Double?,
            var favouriteStatus: Int?,
            val id: Int?,
            val introAffirmation: String?,
            val liveStatus: Int?,
            val musicDuration: Double?,
            val scheduleTime: String?,
            val status: Int?,
            val thumbnail: String?,
            val title: String?,
            val transcript: Any?,
            val type: Int?=0,
            val updated_at: String?,
            val views: Int?
        ) {
            data class Category(
                val CID: Int?,
                val icon: String?,
                val name: String?,
                val subCategory: List<SubCategory?>?
            ) {
                data class SubCategory(
                    val SCID: Int?,
                    val icon: String?,
                    val name: String?
                )
            }

            data class Customise(
                val CID: Any?,
                val MID: Any?,
                val backgroundDuration: Int?,
                val duration: Int?,
                val durationStatus: Int?
            )
        }
    }
}

data class TextAffirmation(
    @SerializedName("background")
    val background: String? = "",
    @SerializedName("CID")
    val cID: Int? = 0,
    @SerializedName("categoryIcon")
    val categoryIcon: String? = "",
    @SerializedName("categoryName")
    val categoryName: String? = "",
    @SerializedName("created_at")
    val createdAt: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("status")
    val status: Int? = 0,
    var favouriteStatus: Int? = 0,
    @SerializedName("UID")
    val uID: Any? = Any()
)