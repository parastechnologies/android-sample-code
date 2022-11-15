package com.app.muselink.data.modals.responses

import com.app.muselink.model.responses.getInterest.InterestsDatum
import com.app.muselink.model.responses.GetGoalsData
import java.io.Serializable

data class UserData(
    val Profile_Image: String? = "",
    val User_Name: String? = "",
    var Favorite_Staus: Int? = 0,
    val Biography: String? = "",
    val Personal_Interest: ArrayList<InterestsDatum>? = null,
    val id: String? = "",
    val Career_Goals: ArrayList<GetGoalsData>? = null
):Serializable

