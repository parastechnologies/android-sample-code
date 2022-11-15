package com.app.muselink.listener

import com.app.muselink.data.modals.responses.UserDetailModel

interface DropBoxUserInfoInterface {
    fun getUserData(socialId:String,socialType:String,countryName:String)
}