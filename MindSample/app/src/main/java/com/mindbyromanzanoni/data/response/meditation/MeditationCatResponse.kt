package com.mindbyromanzanoni.data.response.meditation

import com.mindbyromanzanoni.base.BaseResponse

data class MeditationCatResponse(
    val `data`: ArrayList<MeditationCatListResponse>,
) :BaseResponse()

data class MeditationCatListResponse(
    val categoryImage: String,
    val categoryName: String,
    val courseDuration: String,
    val createdOn: String,
    val medidationCatId: Int,
    val meditationTypes: List<Any>
)