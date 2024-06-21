package com.mindbyromanzanoni.data.response.edification

import com.mindbyromanzanoni.base.BaseResponse

data class EdificationTypeResponse(
    val `data`: ArrayList<EdificationTypeListResponse>,
    ) : BaseResponse()

data class EdificationTypeListResponse(
    val isImage: Boolean? = false,
    val categoryId: Int?=0,
    val categoryName: String?=null,
    val content: String?=null,
    val createdOn: String?=null,
    val duration: String?=null,
    val meditationTypeId: Int?=null,
    val title: String?=null,
    val videoName: String?=null,
    val videoThumbImage: String?=null,
    val videoHlsLink: String?=null
)
