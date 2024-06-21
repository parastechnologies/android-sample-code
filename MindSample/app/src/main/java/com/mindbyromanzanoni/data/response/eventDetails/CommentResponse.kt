package com.mindbyromanzanoni.data.response.eventDetails

import com.mindbyromanzanoni.base.BaseResponse

data class CommentResponse(
    val `data`: ArrayList<CommentListResponse>?,
) : BaseResponse()

data class CommentListResponse(
    val commentDesc: String?=null,
    val commentId: Int?=null,
    val commentedById: Int?,
    val commentedByImage: String? = "",
    val commentedByName: String?= "",
    val commentedOn: String?,
    val eventId: Int?
)