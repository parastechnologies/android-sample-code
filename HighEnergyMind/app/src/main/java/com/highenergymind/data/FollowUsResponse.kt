package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by Puneet on 31/05/24
 */
data class FollowUsResponse(
    @SerializedName("data")
    val `data`: List<FollowUsData>
) : BaseResponse()

data class FollowUsData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("link_img")
    val linkImg: String,
    @SerializedName("link_name")
    val linkName: String,
    @SerializedName("social_link_title")
    val socialLinkTitle: String
)