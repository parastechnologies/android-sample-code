package com.mindbyromanzanoni.data.response.edification

import com.mindbyromanzanoni.base.BaseResponse

data class EdificationCatResponse(
    val `data`: ArrayList<EdificationCatListResponse>?,
) : BaseResponse()

data class EdificationCatListResponse(
    val courseDuration: String,
    val createdOn: String,
    val ediCategoryName: String,
    val edificationCategoryImage: String,
    val edificationId: Int,
    val editificationTypes: List<Any>
)
