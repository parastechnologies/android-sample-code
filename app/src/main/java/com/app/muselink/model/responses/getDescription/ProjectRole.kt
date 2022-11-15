package com.app.muselink.data.modals.responses.getDescription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProjectRole {
    @SerializedName("User_Role_Id")
    @Expose
    var userRoleId: String? = null

    @SerializedName("Audio_Id")
    @Expose
    var audioId: String? = null

    @SerializedName("Role_Id")
    @Expose
    var roleId: String? = null

    @SerializedName("User_Role_Date")
    @Expose
    var userRoleDate: String? = null

    @SerializedName("Project_Role_Id")
    @Expose
    var projectRoleId: String? = null

    @SerializedName("Role_Name")
    @Expose
    var roleName: String? = null

    @SerializedName("ProjectRole_Icon")
    @Expose
    var projectRoleIcon: String? = null

    @SerializedName("Project_Role_Status")
    @Expose
    var projectRoleStatus: String? = null

    @SerializedName("Project_Role_Date")
    @Expose
    var projectRoleDate: String? = null
}