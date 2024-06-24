package com.in2bliss.domain

import android.app.Activity
import com.in2bliss.data.subscriptionImpl.SubscriptionResult
import com.in2bliss.data.subscriptionImpl.SubscriptionStatus
import kotlinx.coroutines.flow.SharedFlow

interface SubscriptionHelperInterface {

    /**
     * Initializing billing client
     * @param activity
     * */
    fun initializingBillingClient(
        activity: Activity,
        productId: String
    )


    /**
     * Getting the subscription status shared flow
     * */
    fun getSubscriptionStatus() : SharedFlow<SubscriptionResult>
}