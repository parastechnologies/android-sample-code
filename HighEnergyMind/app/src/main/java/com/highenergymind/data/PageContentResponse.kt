package com.highenergymind.data
import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse
import com.highenergymind.base.BaseViewModel


/**
 * Created by Puneet on 16/05/24
 */
data class PageContentResponse(

    @SerializedName("data")
    val `data`: PageData

):BaseResponse()

data class PageData(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("slug")
    val slug: String
)