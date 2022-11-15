package com.app.muselink.ui.fragments.chatscreen.modals

abstract class BaseResponseChat {

    var message: String? = null
    var Response: String? = "false"

    fun isSuccess(): Boolean {
        return Response?.let {
            return (Response.equals("true"))
        } ?: false
    }

}