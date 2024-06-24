package com.in2bliss.data.model

import com.in2bliss.data.networkRequest.ApiConstant

data class FavoritesType(
    val title: String,
    var array: ArrayList<String>,
    val type: ApiConstant.ExploreType
)