package com.in2bliss.data.model


import android.os.Parcelable
import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse
import com.in2bliss.data.model.musicList.MusicList
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SeeAllResponse(
    @SerializedName("data")
    val data: List<Data>
) : BaseResponse(), Parcelable {

    @Keep
    @Parcelize
    data class Data(
        @SerializedName("audio")
        val audio: String?,
        @SerializedName("background")
        val background: String?,
        @SerializedName("CID")
        val cID: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("description")
        val description: String?,
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
        var favouriteStatus: Int?,
        val views: Int?,
        val categoryName: String?,
        val duration: Double?,
        val categoryIcon: String?,
        val thumbnail: String?,
        val affirmation: String?,
        val introAffirmation: String?,
        val customise: MusicList.Customise?,
        val audioName: String?,
        var isPlaying : Boolean? =false,
        val transcript : String? = null,
        val audioType :Int? = null
    ) : Parcelable {

        @androidx.annotation.Keep
        @Parcelize
        data class Customise(
            val duration: Int?,
            val durationStatus: Int?,
            val cID: Int?,
            val mID: Int?,
            val music: Music
        ) : Parcelable

        @androidx.annotation.Keep
        @Parcelize
        data class Music(
            val audio: String?,
            val audioName: String?,
            val title: String?,
            val cID: Int?,
            val type: Int?,
            val background: String?,
            val thumbnail: String?
        ) : Parcelable
    }
}