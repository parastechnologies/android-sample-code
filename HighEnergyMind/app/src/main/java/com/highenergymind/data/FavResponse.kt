package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 29/03/24
 */
data class FavResponse(
    @SerializedName("data")
    val data: FavTypes
) : BaseResponse()

data class FavTypes(
    @SerializedName("background_affirmation_img")
    val backgroundAffImg:String?,
    @SerializedName("affirmationList")
    val affirmationList: List<AffDay>,
    @SerializedName("trackList")
    val trackList: List<TrackOb>
)
