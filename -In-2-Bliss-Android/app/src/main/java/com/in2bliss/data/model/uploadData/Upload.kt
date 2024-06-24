package com.in2bliss.data.model.uploadData


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import android.os.Parcelable
import com.in2bliss.base.BaseResponse
import kotlinx.android.parcel.Parcelize

@Keep
@kotlinx.parcelize.Parcelize
data class Upload(
    @SerializedName("fileName")
    val fileName: String?
) : BaseResponse(),Parcelable