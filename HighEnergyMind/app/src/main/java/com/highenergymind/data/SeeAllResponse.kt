package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 11/04/24
 */
data class SeeAllResponse(
    @SerializedName("data")
    val data: List<TrackOb>
) : BaseResponse()
