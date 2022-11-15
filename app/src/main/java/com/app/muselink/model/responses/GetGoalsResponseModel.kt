package com.app.muselink.model.responses

import com.app.muselink.model.BaseResponse
import java.io.Serializable

class GetGoalsResponseModel : BaseResponse() {
    var data: ArrayList<GetGoalsData>? = null
}
class GetGoalsData :Serializable {
    var Goal_Id: String? = null
    var Goal_Name: String? = null
    var Goal_Status: String? = null
    var Goal_Date: String? = null
    var Goal_Icon: String? = null
    var IsSelected: Boolean = false
}