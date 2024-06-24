package com.in2bliss.data.model.musicCateogries


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.in2bliss.base.BaseResponse

@Keep
@Parcelize
data class MusicCategories(
    @SerializedName("data")
    val `data`: List<Data?>?
) :BaseResponse(), Parcelable {
    @Keep
    @Parcelize
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("favouriteStatus")
        val favouriteStatus: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?
    ) : Parcelable
}