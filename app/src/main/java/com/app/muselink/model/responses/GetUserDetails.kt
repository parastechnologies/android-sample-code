package com.app.muselink.model.responses

class GetUserDetails {

    var name: String? = ""
    var firstname: String? = ""
    var lastname: String? = ""
    var profileImage: String? = ""
    var gender: String? = ""
    var location: String? = ""
    var User_Name: String? = ""
    var id: String? = ""
    var Chat_Uniq_Number: String? = ""
    var phone: String? = ""
    var email: String? = ""
    var firebaseToken: String? = ""
    var countryName: String? = ""

    fun isHaveUserId(): Boolean {
        return id?.trim() != ""
    }

}