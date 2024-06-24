package com.in2bliss.ui.activity.auth.appStatus

import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.subscriptionImpl.SubscriptionHelperImpl
import com.in2bliss.domain.SubscriptionHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppStatusViewModel @Inject constructor() : BaseViewModel() {

    var subscriptionHelper: SubscriptionHelperInterface? = null

    fun initializingSubscriptionHelper() {
        if (subscriptionHelper == null) {
            subscriptionHelper = getSubscriptionInstance()
        }
    }

    private fun getSubscriptionInstance(): SubscriptionHelperInterface {
        return SubscriptionHelperImpl()
    }

    override fun retryApiRequest(apiName: String) {

    }
}