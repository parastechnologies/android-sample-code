package com.mindbyromanzanoni.data.response.userMessageList

data class MessageResponse(
    val Messages: ArrayList<MessageListResponse>? = arrayListOf(),
    val OtherUserId: Int,
    val OtherUserImage: String,
    val OtherUserName: String
)

data class MessageListResponse(
    val ChatId: Int?=-1,
    val Image: String?="",
    val Message: String?="",
    val MessageOn: String?="",
    val MessageType: String?="",
    val Name: String?="",
    val OtherUserId: Int?=-1,
    val UserId: Int?=0,
    val status: Int?=0,
    var IsRead: Boolean? = false,
)
