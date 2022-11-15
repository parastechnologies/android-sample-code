package com.app.muselink.data.modals.responses.getDescription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDescriptionData {
    @SerializedName("Description")
    @Expose
    var description: String? = null

    @SerializedName("Description_Color")
    @Expose
    var descriptionColor: String? = null

    @SerializedName("projectRoles")
    @Expose
    var projectRoles: List<ProjectRole>? = null

    @SerializedName("projectGoals")
    @Expose
    var projectGoals: List<ProjectGoal>? = null
}