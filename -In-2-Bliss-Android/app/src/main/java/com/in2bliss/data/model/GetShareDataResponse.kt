package com.in2bliss.data.model

import android.os.Parcelable
import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse
import com.in2bliss.data.model.musicList.MusicList
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GetShareDataResponse(
    val `data`: Data?
) : BaseResponse(), Parcelable {

    @androidx.annotation.Keep
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
        val customise: MusicList.Customise? = null,
        val audioName: String? = null,
        val transcript: String? = null,
        val audioType: Int? = null,
        val background: String? = null,
        val createdBy: Int? = null,
        val category: List<FavouritesResponse.Category>? = null,
    ) : Parcelable
}