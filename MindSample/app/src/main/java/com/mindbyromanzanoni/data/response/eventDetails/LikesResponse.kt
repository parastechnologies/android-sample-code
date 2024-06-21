package com.mindbyromanzanoni.data.response.eventDetails

import com.mindbyromanzanoni.base.BaseResponse

data class LikesResponse(
    val `data`: ArrayList<LikesListResponse>,
) :BaseResponse()

data class LikesListResponse(
    val favEventId: Any,
    val userId: Any,
    val userImage: String,
    val userName: String
)