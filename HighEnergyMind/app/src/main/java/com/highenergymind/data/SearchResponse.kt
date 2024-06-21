package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 17/04/24
 */
data class SearchResponse(
    @SerializedName("data")
    val data: SearchTypes
) : BaseResponse()

data class SearchTypes(
    @SerializedName("affirmationList")
    val affirmationList: List<AffDay>,
    @SerializedName("trackList")
    val trackList: List<TrackOb>
)

