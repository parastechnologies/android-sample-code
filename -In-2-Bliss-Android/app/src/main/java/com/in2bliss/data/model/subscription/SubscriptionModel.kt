package com.in2bliss.data.model.subscription

data class SubscriptionModel(
    var title : String?,
    var name: String,
    var price: String,
    var countryDollar:String,
    var productId: String? = null,
    var selected: Boolean = false
)
