package com.in2bliss.data.model.musicList


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse
import com.in2bliss.data.model.FavouritesResponse
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MusicList(
    @SerializedName("data")
    val `data`: List<Data>?,
    @SerializedName("searchHistory")
    val searchHistory: List<SearchHistory?>?,
    @SerializedName("streak")
    val streak:Int?,
) : BaseResponse(), Parcelable {
    @Keep
    @Parcelize
    data class Data(
        @SerializedName("data")
        val `data`: List<Data?>?,
        @SerializedName("title")
        val title: String?,
        val type: Int?,
        val dataType: Int?
    ) : Parcelable {
        @Keep
        @Parcelize
        data class Data(
            @SerializedName("audio")
            val audio: String? = null,
            @SerializedName("CID")
            val cID: Int? = null,
            @SerializedName("categoryIcon")
            val categoryIcon: String? = null,
            @SerializedName("categoryName")
            val categoryName: String? = null,
            @SerializedName("created_at")
            val createdAt: String? = null,
            @SerializedName("description")
            val description: String? = null,
            @SerializedName("duration")
            val duration: Double? = null,
            @SerializedName("favouriteStatus")
            var favouriteStatus: Int? = null,
            @SerializedName("id")
            val id: Int? = null,
            @SerializedName("liveStatus")
            val liveStatus: Int? = null,
            @SerializedName("SCID")
            val sCID: Int? = null,
            @SerializedName("scheduleTime")
            val scheduleTime: String? = null,
            @SerializedName("status")
            val status: Int? = null,
            @SerializedName("title")
            val title: String? = null,
            @SerializedName("type")
            val type: Int? = null,
            @SerializedName("updated_at")
            val updatedAt: String? = null,
            @SerializedName("views")
            val views: Int? = null,
            val thumbnail: String? = null,
            val affirmation: String? = null,
            val introAffirmation: String? = null,
            val customise: Customise? = null,
            val audioName: String? = null,
            val transcript: String? = null,
            val audioType: Int? = null,
            val background: String? = null,
            val category: List<FavouritesResponse.Category>? = null,
        ) : Parcelable
    }

    @Keep
    @Parcelize
    data class Customise(
        val duration: Int?,
        val backgroundDuration: Int?,
        val durationStatus: Int?,
        val CID: Int?,
        val MID: Int?,
        val music: Music?
    ) : Parcelable

    @Keep
    @Parcelize
    data class Music(
        val audio: String?,
        val audioName: String?,
        val title: String?,
        val CID: Int?,
        val type: Int?,
        val thumbnail: String?
    ) : Parcelable

    @Keep
    @Parcelize
    data class SearchHistory(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("text")
        val text: String?
    ) : Parcelable
}