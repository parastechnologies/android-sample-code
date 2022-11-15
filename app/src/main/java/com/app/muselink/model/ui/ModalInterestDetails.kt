package com.app.muselink.model.ui

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ModalInterestDetails(

    @field:SerializedName("Interest_Id")
    val interestId: String? = null,

    @field:SerializedName("Interest_Name")
    val interestName: String? = null,

    @field:SerializedName("Interest_Status")
    val interestStatus: String? = null,

    @field:SerializedName("Interest_Date")
    val interestDate: String? = null,

    @field:SerializedName("Interests_Icon")
    val interestIcon: String? = null,

    var IsSelected: Boolean? = null


) : Serializable
