package com.app.muselink.data.modals.responses.getInterest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.app.muselink.model.responses.getInterest.InterestsDatum

class GetInterestResponseData {
    @SerializedName("Interests_Category_Id")
    @Expose
    var interestsCategoryId: String? = null

    @SerializedName("Interests_Category_Name")
    @Expose
    var interestsCategoryName: String? = null

    @SerializedName("interestsData")
    @Expose
    var interestsData: ArrayList<InterestsDatum>? = null
}