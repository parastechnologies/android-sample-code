package com.in2bliss.data.model.journalStreak


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
data class JournalStreak(
    @SerializedName("data")
    val `data`: List<Data?>?,
    val streak : Streak
) : Parcelable {

    @Keep
    @Parcelize
    data class Streak(
        val currentStreak : Int?,
        val totalEntries : Int?,
        val maxStreak : Int?
    ) : Parcelable
    @Keep
    @Parcelize
    data class Data(
        @SerializedName("background")
        val background: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("UID")
        val uID: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?
    ) : Parcelable
}