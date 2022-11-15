package com.app.muselink.data.modals.responses.getDescription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProjectGoal {
    @SerializedName("User_Goal_Id")
    @Expose
    var userGoalId: String? = null

    @SerializedName("Audio_Id")
    @Expose
    var audioId: String? = null

    @SerializedName("Goal_Id")
    @Expose
    var goalId: String? = null

    @SerializedName("UserGoal_Date")
    @Expose
    var userGoalDate: String? = null

    @SerializedName("Goal_Name")
    @Expose
    var goalName: String? = null

    @SerializedName("Goal_Icon")
    @Expose
    var goalIcon: String? = null

    @SerializedName("Goal_Status")
    @Expose
    var goalStatus: String? = null

    @SerializedName("Goal_Date")
    @Expose
    var goalDate: String? = null
}