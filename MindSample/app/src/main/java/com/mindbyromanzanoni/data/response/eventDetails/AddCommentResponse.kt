package com.mindbyromanzanoni.data.response.eventDetails

import com.mindbyromanzanoni.base.BaseResponse

data class AddCommentResponse(
    val `data`: CommentListResponse,
) :BaseResponse()
