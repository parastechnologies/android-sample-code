package com.app.muselink.data.modals.responses

import com.app.muselink.model.BaseResponse

class GetRoleResponseModel: BaseResponse() {
    var data: List<GetRoleData>? = null
}
class GetRoleData {
    var Project_Role_Id: String? = null
    var Role_Name: String? = null
    var Project_Role_Status: String? = null
    var Project_Role_Date: String? = null
    var IsSelected: Boolean = false
}