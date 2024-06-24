package com.in2bliss.data.subscriptionImpl

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.in2bliss.domain.SubscriptionHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionHelperImpl : SubscriptionHelperInterface {

    private var billingClient: BillingClient? = null
    private var job: Job? = null
    private val mutablePurchaseListener by lazy {
        MutableSharedFlow<SubscriptionResult>()
    }

    /**
     * Initializing billing client
     * @param activity
     * @param productId of the product created in google play console
     * */
    override fun initializingBillingClient(activity: Activity, productId: String) {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()

        establishConnection(
            productId = productId,
            activity = activity
        )
    }

    /**
     * Getting the subscription status shared flow
     * */
    override fun getSubscriptionStatus(): SharedFlow<SubscriptionResult> {
        return mutablePurchaseListener.asSharedFlow()
    }

    /**
     * Launching the purchase flow
     * @param activity
     * */
    private fun launchPurchaseFlow(
        activity: Activity,
        productList: MutableList<ProductDetails>
    ) {

        val productDetailsParamList: MutableList<BillingFlowParams.ProductDetailsParams> =
            mutableListOf()

        productList.forEachIndexed { index, productDetails ->
            productDetailsParamList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(
                        productDetails.subscriptionOfferDetails?.get(index)?.offerToken ?: ""
                    )
                    .build()
            )
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamList).setIsOfferPersonalized(true)
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    private val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchaseList ->

        when (billingResult.responseCode) {

            BillingClient.BillingResponseCode.OK -> {
                launchInMain {
                    withContext(Dispatchers.IO) {
                        purchaseList?.forEach { purchase ->
                            acknowledgePurchase(purchase)
                        }
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                launchInMain {
                    mutablePurchaseListener.emit(
                        value = SubscriptionResult(
                            billingDetails = null,
                            subscriptionStatus = SubscriptionStatus.CANCELLED
                        )
                    )
                }
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                launchInMain {
                    mutablePurchaseListener.emit(
                        value = SubscriptionResult(
                            billingDetails = null,
                            subscriptionStatus = SubscriptionStatus.ALREADY_PURCHASED
                        )
                    )
                }
            }

            else -> {
                launchInMain {
                    mutablePurchaseListener.emit(
                        value = SubscriptionResult(
                            billingDetails = null,
                            subscriptionStatus = SubscriptionStatus.ERROR
                        )
                    )
                }
            }
        }
    }

    /**
     * Establishing the connection to google play console
     * @param productId
     * @param activity
     * */
    private fun establishConnection(
        productId: String,
        activity: Activity
    ) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.d("Google_play_subscription", " onBillingServiceDisconnected")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                queryPurchases()
                queryProductDetails(
                    productId = productId,
                    activity = activity
                )
            }
        })
    }

    /**
     * Acknowledge the purchase
     * @param purchase item of the purchase list
     * */
    private fun acknowledgePurchase(
        purchase: Purchase
    ) {
        val acknowledgedParam = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
        billingClient?.acknowledgePurchase(acknowledgedParam.build()) {
            launchInMain {
                mutablePurchaseListener.emit(
                    value = SubscriptionResult(
                        billingDetails = purchase,
                        subscriptionStatus = SubscriptionStatus.PURCHASED
                    )
                )
            }
        }
    }

    /**
     * Getting the list of product which are added in google play console for subscription e.g ( monthly plan or yearly plan)
     * @param productId
     * @param activity
     * */
    private fun queryProductDetails(
        productId: String,
        activity: Activity
    ) {
        val queryProductDetailParam = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient?.queryProductDetailsAsync(queryProductDetailParam) { _, productDetailList ->
            launchPurchaseFlow(
                activity = activity,
                productList = productDetailList
            )
        }
    }

    /**
     * Query previous purchase list ( Previous purchases of the user )
     * */
    private fun queryPurchases() {
        if (billingClient?.isReady == false) {
            return
        }
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
        ) { _, _ ->

        }
    }

    /**
     * Launching coroutines in main thread
     * */
    private fun launchInMain(body: suspend () -> Unit) {
        if (job != null) job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            body.invoke()
        }
    }
}

enum class SubscriptionStatus {
    PURCHASED,
    ALREADY_PURCHASED,
    CANCELLED,
    ERROR
}

data class SubscriptionResult(
    val billingDetails: Purchase?,
    val subscriptionStatus: SubscriptionStatus
)