package com.app.muselink.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Message
import com.android.billingclient.api.*
import java.util.logging.Handler
import kotlin.collections.ArrayList

class InAppPurchase(val activity: Activity, val inAppPurchaseNavigator: InAppPurchaseNavigator) {

    enum class BillingType(val value: String) {
        Subscription("subscription"),
        Product("product")
    }

    enum class SubscriptionType(val value: String) {
        Monthly("monthly"),
        Yearly("yearly")
    }

    interface InAppPurchaseNavigator {
        fun onPurchaseSuccess(purchase: Purchase)
        fun onPurchaseFailed(message: String)
        fun listSkuDetails(skuDetailsArrayList: ArrayList<SkuDetails>?)
    }

    private var readyToPurchase = false
    var billingClient: BillingClient? = null
    var arrayListOfProductIds = ArrayList<String>()
    var skuDetailsArrayList: ArrayList<SkuDetails>? = null
    var selectedSkuDetails: SkuDetails? = null
    var purchase: Purchase? = null

    var purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                for (purchase in purchases) {
                    if (purchase.skus.equals(selectedSkuDetails?.getSku())) {
//                        UpdateDetailsInFireBase(purchase)
                        this.purchase = purchase
                        break
                    }
                }
                val message = Message()
                message.what = 2
                mHandler.sendMessage(message)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                val message = Message()
                message.what = 3
                mHandler.sendMessage(message)
            } else {

            }
        }

    private fun getSkuDetails(productId: String): SkuDetails? {
        var skuDetails: SkuDetails? = null
        if (skuDetailsArrayList != null && skuDetailsArrayList!!.size > 0) {
            for (i in skuDetailsArrayList!!.indices) {
                if (skuDetailsArrayList!![i].sku.equals(productId, ignoreCase = true)) {
                    skuDetails = skuDetailsArrayList!![i]
                    break
                }
            }
        }
        return skuDetails
    }

    fun purchase(productId: String) {
        if (skuDetailsArrayList.isNullOrEmpty().not()) {
            selectedSkuDetails = getSkuDetails(productId)
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(selectedSkuDetails!!)
                .build()
            val responseCode =
                billingClient!!.launchBillingFlow(activity, billingFlowParams).responseCode
        }
    }

    fun getDetailsofPurchases() {

        val params = SkuDetailsParams.newBuilder()
        if (type.equals(BillingType.Subscription.value)) {
            params.setSkusList(arrayListOfProductIds).setType(BillingClient.SkuType.SUBS)
        } else {
            params.setSkusList(arrayListOfProductIds).setType(BillingClient.SkuType.INAPP)
        }

        billingClient!!.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            val list = ArrayList<SkuDetails>()
            if (skuDetailsList != null) {
                for (i in skuDetailsList.indices) {
                    list.add(skuDetailsList[i])
                }
            }
            skuDetailsArrayList = list
            if (skuDetailsArrayList.isNullOrEmpty().not()) {

                val message = Message()
                message.what = 1
                mHandler.sendMessage(message)
            }
        }
    }

    @SuppressLint("HandlerLeak")
    var mHandler = object : android.os.Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    inAppPurchaseNavigator.listSkuDetails(skuDetailsArrayList)
                }
                2 -> {
                    inAppPurchaseNavigator.onPurchaseSuccess(purchase!!)
                }
                3 -> {
                    inAppPurchaseNavigator.onPurchaseFailed("Try Again Later")
                }
            }
        }
    }

    fun getPriceAccordingProductId(productId: String): String {
        var priceText = ""
        for (i in 0 until skuDetailsArrayList!!.size) {
            if (skuDetailsArrayList!![i].sku.equals(productId, ignoreCase = true)) {
                priceText = skuDetailsArrayList!![i].price
                break
            }
        }
        return priceText
    }

    var type: String? = ""

    fun intializeBillingclient(type: String, productIds: ArrayList<String>?) {

        arrayListOfProductIds = productIds!!
        this.type = type

        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                readyToPurchase = false
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    readyToPurchase = true
                    getDetailsofPurchases()
                }
            }
        })

    }

}