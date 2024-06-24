package com.in2bliss.data.model.musicCateogries


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MusicList(
    @SerializedName("data")
    val `data`: List<Data?>?
) : BaseResponse(), Parcelable {
    @Keep
    @Parcelize
    data class Data(
        @SerializedName("audio")
        val audio: String?,
        @SerializedName("audioName")
        val audioName: String?,
        @SerializedName("CID")
        val cID: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("type")
        val type: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("duration")
        val duration: Double?,
        val thumbnail : String?,
        var favouriteStatus: Int? = null
    ) : Parcelable
}