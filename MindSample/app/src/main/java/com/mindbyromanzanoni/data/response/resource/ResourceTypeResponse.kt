package com.mindbyromanzanoni.data.response.resource

import com.google.gson.annotations.SerializedName
import com.mindbyromanzanoni.base.BaseResponse

data class ResourceTypeResponse(
    val `data`: ArrayList<ResourceTypeList>,
) : BaseResponse()

data class ResourceTypeList(
    val content: String="",
    val duration: String?="",
    val createdOn: String="",
    @SerializedName(value="imageName" , alternate = ["thumbImage"])
    val imageName: String?="",
    @SerializedName(value="audioName" , alternate = ["videoName"])
    val audioName: String="",
    val pdfFileName: String?="",
    val resourceType: Any?=null,
    val resourcesId: Int?=null,
    val resourcesType: Any?=null,
    val resourcesTypeId: Any?=null,
    val title: String=""
)
