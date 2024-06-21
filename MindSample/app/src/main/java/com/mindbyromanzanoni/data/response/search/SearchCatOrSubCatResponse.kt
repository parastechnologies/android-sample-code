package com.mindbyromanzanoni.data.response.search

import com.mindbyromanzanoni.base.BaseResponse

data class SearchCatOrSubCatResponse(
    val `data`: ArrayList<SearchCatOrSubCatListResponse>? = arrayListOf(),
) :BaseResponse()

data class SearchCatOrSubCatListResponse(
    val categoryId: Int,
    val categoryName: String?= null,
    val content: String,
    val createdOn: String,
    val duration: String,
    val videoName: String,
    val videoHlsLink:String?="",
    val id: Int,
    val thumbImage: String,
    val title: String?=null,
    val typeId: Int
)