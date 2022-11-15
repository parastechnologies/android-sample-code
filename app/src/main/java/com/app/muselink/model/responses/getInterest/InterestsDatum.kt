package com.app.muselink.model.responses.getInterest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InterestsDatum: Serializable {
    @SerializedName("Interest_Id")
    @Expose
    var interestId: String? = null

    @SerializedName("Interests_Category_Id")
    @Expose
    var interestsCategoryId: String? = null

    @SerializedName("Interest_Name")
    @Expose
    var interestName: String? = null

    @SerializedName("Interests_Icon")
    @Expose
    var interestsIcon: String? = null

    @SerializedName("Interest_Status")
    @Expose
    var interestStatus: String? = null

    @SerializedName("Interest_Date")
    @Expose
    var interestDate: String? = null

    var isSelected: Boolean = false

}