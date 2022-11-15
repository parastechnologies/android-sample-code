package com.app.muselink.model

abstract class BaseResponse {

    var message: String? = null
    var status: String? = null
    var token: String? = null

    fun isSuccess(): Boolean {
        return status?.let {
            return (status.equals("200"))
        } ?: false
    }
}