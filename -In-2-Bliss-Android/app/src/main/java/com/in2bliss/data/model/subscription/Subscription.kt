package com.in2bliss.data.model.subscription


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Subscription(
    @SerializedName("data")
    val `data`: Data?
) : BaseResponse(), Parcelable {
    @Keep
    @Parcelize
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("isCancel")
        val isCancel: Int?,
        @SerializedName("planType")
        val planType: String?,
        @SerializedName("startDate")
        val startDate: String?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("transactionID")
        val transactionID: String?,
        @SerializedName("UID")
        val uID: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?
    ) : Parcelable
}