package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by Puneet on 15/05/24
 */
data class SeeAllMusicResponse(
    @SerializedName("data")
    val `data`: List<BackAudios>
):BaseResponse()
