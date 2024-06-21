package com.mindbyromanzanoni.data.response.allCategoryDetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindbyromanzanoni.base.BaseResponse

class AllTypeCategoryDetail: BaseResponse() {

    @SerializedName("data")
    @Expose
    var data: Data? = null
}
