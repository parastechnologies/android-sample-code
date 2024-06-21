package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by Puneet on 30/05/24
 */
data class RecentListResponse(
    @SerializedName("data")
    val data:List<TrackOb>
):BaseResponse()
