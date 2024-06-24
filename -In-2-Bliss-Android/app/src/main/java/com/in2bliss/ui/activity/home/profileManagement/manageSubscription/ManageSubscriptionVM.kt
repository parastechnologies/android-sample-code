package com.in2bliss.ui.activity.home.profileManagement.manageSubscription

import com.in2bliss.BuildConfig
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.subscription.Subscription
import com.in2bliss.data.model.subscription.SubscriptionModel
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.data.subscriptionImpl.SubscriptionHelperImpl
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.domain.SubscriptionHelperInterface
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ManageSubscriptionVM @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
    private val sharedPreference: SharedPreference
) : BaseViewModel() {

    var subscriptionHelper: SubscriptionHelperInterface? = null
    var planType: String? = null
    var transactionID: String? = null
    var productId: String? = null
    var navigationFlow: AppConstant.SubscriptionStatus? = null
    var isSubscribed: Boolean = false

    val adapter: SubscriptionAdapter by lazy {
        SubscriptionAdapter()
    }


    private val mutableSubscriptionStatus by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val subscriptionStatus by lazy { mutableSubscriptionStatus.asSharedFlow() }

    private val mutableGetSubscriptionStatus by lazy {
        MutableSharedFlow<Resource<Subscription>>()
    }
    val getSubscriptionStatus by lazy { mutableGetSubscriptionStatus.asSharedFlow() }

    fun initializingSubscriptionHelper() {
        if (subscriptionHelper == null) {
            subscriptionHelper = getSubscriptionInstance()
        }
    }

    private fun getSubscriptionInstance(): SubscriptionHelperInterface {
        return SubscriptionHelperImpl()
    }

    fun getSubscriptionList(monthly: String?=null, yearly: String?=null,
                            countrySymbol:String?=null): ArrayList<SubscriptionModel> {
        return arrayListOf(
            SubscriptionModel(
                title = "Monthly plan",
                name = "/mo",
                productId = BuildConfig.MONTHLY_PRODUCT_ID,
                price = monthly?:"",
                countryDollar = countrySymbol?:""
            ),
            SubscriptionModel(
                title = "Yearly plan",
                name = "/year",
                productId = BuildConfig.YEARLY_PRODUCT_ID,
                price = yearly?:"",
                countryDollar = countrySymbol?:""
            )
        )
    }

    /**
     * Updating subscription bought status to backend to evaluate
     * */
    private fun subscribe() {
        networkCallIo {

            val userData = sharedPreference.userData
            userData?.data?.isSubscriptionApiHit = false
            userData?.data?.purchaseToken = transactionID.orEmpty()
            userData?.data?.planType = planType.orEmpty()
            sharedPreference.userData = userData

            val hasMap = HashMap<String, String>()
            hasMap[ApiConstant.PLAN_TYPE] = planType.orEmpty()
            hasMap[ApiConstant.TRANSACTION_ID] = transactionID.orEmpty()

            mutableSubscriptionStatus.emit(Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.subscribe(
                        body = hasMap
                    )
                },
                apiName = ApiConstant.SUBSCRIBE
            )
            mutableSubscriptionStatus.emit(value = response)
        }
    }

    private fun getSubscriptionStatus() {
        networkCallIo {
            mutableGetSubscriptionStatus.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getSubscriptionStatus()
                },
                apiName = ApiConstant.SUBSCRIPTION_STATUS
            )
            mutableGetSubscriptionStatus.emit(value = response)
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.SUBSCRIBE -> subscribe()
            ApiConstant.SUBSCRIPTION_STATUS -> getSubscriptionStatus()
        }
    }
}