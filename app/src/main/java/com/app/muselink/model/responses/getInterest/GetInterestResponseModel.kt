package com.app.muselink.data.modals.responses.getInterest

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.app.muselink.model.BaseResponse

class GetInterestResponseModel: BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: List<GetInterestResponseData>? = null
}