package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 12/03/24
 */
data class GetReminderResponse(

    @SerializedName("data")
    val `data`: ReminderData?

) : BaseResponse()

