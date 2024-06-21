package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by Puneet on 03/06/24
 */
data class MusicDetailResponse(

    @SerializedName("data")
    val data: BackAudios
) : BaseResponse()