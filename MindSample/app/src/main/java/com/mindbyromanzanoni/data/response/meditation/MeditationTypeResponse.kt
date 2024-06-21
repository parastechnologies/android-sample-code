package com.mindbyromanzanoni.data.response.meditation

import com.google.gson.annotations.SerializedName
import com.mindbyromanzanoni.base.BaseResponse

data class MeditationTypeResponse(
    val `data`: ArrayList<MeditationTypeListResponse>) :BaseResponse()
data class MeditationTypeListResponse(
    val categoryId: Int=0,
    val categoryName: String="",
    val content: String="",
    val createdOn: String="",
    val duration: String="",
    val meditationTypeId: Int=0,
    val title: String="",
    @SerializedName(value="videoName" , alternate = ["fileName"])
    val videoName: String="",
    @SerializedName(value="thumbImage" , alternate = ["videoThumbImage"])
    val videoThumbImage: String=""
)