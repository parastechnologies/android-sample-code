package com.mindbyromanzanoni.data.response.chatUsers

import com.mindbyromanzanoni.base.BaseResponse

data class ChatUsersResponse(
    val `data`: ArrayList<ChatUsers>,
) : BaseResponse()

data class ChatUsers(
    val name: String,
    val email: String,
    val userId: Int,
    val userImage: String
)