package com.app.muselink.model.responses

import com.app.muselink.model.BaseResponse

class SubscriptionStatusRes : BaseResponse(){

    val data : SubscriptionData? = null

}

class SubscriptionData{

    var subscriptionStatus:  Int? = 0

}