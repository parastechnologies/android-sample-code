package com.app.muselink.data.modals.responses

import com.app.muselink.model.BaseResponse

class GetUserListingRes : BaseResponse(){

    val data = ArrayList<UserData>()

    val subscriptionStatus : Int? = 0

}