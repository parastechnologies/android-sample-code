package com.in2bliss.data.model


import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse

data class AffirmationListResponse(
    @SerializedName("data")
    val `data`: List<Data>
) : BaseResponse() {
    data class Data(
        @SerializedName("audio")
        val audio: String?,
        @SerializedName("audioType")
        val audioType: Any,
        @SerializedName("background")
        val background: String?,
        @SerializedName("category")
        val category: Category?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("transcript")
        val transcript: Any,
        @SerializedName("UID")
        val uID: Any,
        var favouriteStatus : Int?,
        val thumbnail : String?
    ) {
        data class Category(
            @SerializedName("CID")
            val cID: Int?,
            @SerializedName("icon")
            val icon: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("subCategory")
            val subCategory: List<SubCategory>,
            val id : Int?
        ) {
            data class SubCategory(
                @SerializedName("icon")
                val icon: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("SCID")
                val sCID: Int?,
                val id : Int?
            )
        }
    }
}