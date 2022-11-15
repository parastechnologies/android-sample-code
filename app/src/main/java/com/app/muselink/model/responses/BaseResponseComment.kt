package com.app.muselink.data.modals.responses

abstract class BaseResponseComment {

    var message: String? = null
    var Response: Boolean? = false

    fun isSuccess(): Boolean {
        return Response?.let {
            return (Response == true)
        } ?: false
    }

}