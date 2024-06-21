package com.highenergymind.data
import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by Puneet on 30/05/24
 */
data class GetChoiceResponse(

    @SerializedName("data")
    val `data`: List<ChoiceData>

):BaseResponse()

data class ChoiceData(
    @SerializedName("choice_img")
    val choiceImg: String,
    @SerializedName("choice_img_active")
    val choiceImgActive: String,
    @SerializedName("choice_img_active_path")
    val choiceImgActivePath: String,
    @SerializedName("choice_img_path")
    val choiceImgPath: String,
    @SerializedName("choice_name")
    val choiceName: String,
    @SerializedName("id")
    val id: Int,
    var isChecked:Boolean?=false
)