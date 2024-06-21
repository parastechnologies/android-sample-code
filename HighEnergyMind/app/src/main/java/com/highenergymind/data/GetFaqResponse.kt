package com.highenergymind.data
import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 14/03/24
 */
data class GetFaqResponse(
    @SerializedName("data")
    val `data`: List<Faq>
):BaseResponse()

data class Faq(
    @SerializedName("answer")
    val answer: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("question")
    val question: String
)