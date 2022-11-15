package com.app.muselink.ui.bottomsheets.musiclinkpro

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.android.billingclient.api.Purchase
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.model.responses.SubscriptionRes
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class MuselinkProViewModel@ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    interface MuselinkProViewModelNavigator{
        fun onSuccessApi(subscriptionRes: SubscriptionRes)
    }

    var viewLifecycleOwner : LifecycleOwner?  =null

    private val requestApi = MutableLiveData<HashMap<String, String>>()

    private val _subscription = requestApi.switchMap { requestApi ->
        repository.subscription(requestApi)
    }

    var muselinkProViewModelNavigator: MuselinkProViewModelNavigator? = null

    fun addListener(muselinkProViewModelNavigator: MuselinkProViewModelNavigator){
        this.muselinkProViewModelNavigator =muselinkProViewModelNavigator
    }

    fun callApiSubscription(purchase: Purchase,paymentId:String,product:String,amount:String){
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.USER_ID.value] =
            SharedPrefs.getUser().id.toString()
        request[SyncConstants.APIParams.PAYMENT_ID.value] = paymentId
        request[SyncConstants.APIParams.TRANSACTION_ID.value] = purchase.orderId
        request[SyncConstants.APIParams.AMOUNT.value] = amount
        request[SyncConstants.APIParams.PRODUCT.value] = product
        requestApi.value = request
    }

    val subscriptionResponse: LiveData<Resource<SubscriptionRes>> = _subscription

    fun setSubscriptionObserver(){

        subscriptionResponse?.observe(viewLifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data!!.isSuccess()) {
                        muselinkProViewModelNavigator?.onSuccessApi(it.data)
                    } else {
                        showToast(activity, it.data.message)
                    }
                }

                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(activity, it.message)
                }

                Resource.Status.LOADING
                -> {
                    showLoader()
                }
            }
        })

    }


}