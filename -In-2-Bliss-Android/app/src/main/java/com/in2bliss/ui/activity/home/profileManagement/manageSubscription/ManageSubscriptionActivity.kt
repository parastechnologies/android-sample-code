package com.in2bliss.ui.activity.home.profileManagement.manageSubscription

import android.app.Activity
import android.content.Context
import android.telephony.TelephonyManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.data.subscriptionImpl.SubscriptionStatus
import com.in2bliss.databinding.ActivityManageSubscriptionBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ManageSubscriptionActivity : BaseActivity<ActivityManageSubscriptionBinding>(
    layout = R.layout.activity_manage_subscription
) {

    @Inject
    lateinit var sharedPreference: SharedPreference
    private var monthly = ""
    private var yearly = ""
    private var countrySymbol = ""

    private val viewModel: ManageSubscriptionVM by viewModels()

    override fun init() {
        binding.data = viewModel
        binding.toolBar.tvTitle.text = getString(R.string.pricing_plan)
        getBundleData()
        settingRecyclerView()
        viewModel.initializingSubscriptionHelper()
        onClick()
        observer()

        if (viewModel.navigationFlow != AppConstant.SubscriptionStatus.SUBSCRIPTION_EXPIRED) {
            viewModel.retryApiRequest(
                apiName = ApiConstant.SUBSCRIPTION_STATUS
            )
        }
    }

    private fun getBundleData() {
        intent.getStringExtra(AppConstant.SUBSCRIPTION).let { navigation ->
            viewModel.navigationFlow = when (navigation) {
                AppConstant.SubscriptionStatus.SUBSCRIPTION_EXPIRED.name -> {
                    AppConstant.SubscriptionStatus.SUBSCRIPTION_EXPIRED
                }

                else -> AppConstant.SubscriptionStatus.SIGN_UP_FLOW
            }
        }
    }

    private fun settingRecyclerView() {
        convertPriceWithCountryBase()
        binding.rvRecyclerview.adapter = viewModel.adapter
        viewModel.adapter.submitList(viewModel.getSubscriptionList(monthly,yearly,countrySymbol))
        viewModel.adapter.listener = { productId, position ->
            viewModel.productId = productId
            viewModel.adapter.currentList.forEachIndexed { index, _ ->
                viewModel.adapter.currentList[index].selected = index == position
                viewModel.adapter.notifyItemChanged(index)
            }
        }
    }

    private fun convertPriceWithCountryBase() {
        when (getCountryCode(this)) {
            "IN" -> {
                monthly = "490.00"
                yearly = "4359.82"
                countrySymbol="₹"
            }

            "CA" -> {
                monthly = "7.96"
                yearly = "70.85"
                countrySymbol="CAD $"
            }

            "US" -> {
                monthly = "5.89"
                yearly = "52.44"
                countrySymbol="$"
            }

            "NZ" -> {
                monthly = "9.55"
                yearly = "84.98"
                countrySymbol="NZ $"
            }

            "GB" -> {
                monthly = "4.65"
                yearly = "41.40"
                countrySymbol="£"
            }

            "AU" -> {
                monthly = "8.99"
                yearly = "79.99"
                countrySymbol="AU $"
            }
            "ZA" -> {
                monthly = "113.56"
                yearly = "1010.40"
                countrySymbol="ZAR"
            }

            else -> {
                monthly = "8.99"
                yearly = "79.99"
                countrySymbol="AU $"
            }
        }

    }


    private fun observer() {
        lifecycleScope.launch {
            viewModel.subscriptionHelper?.getSubscriptionStatus()
                ?.collectLatest { subscriptionResult ->
                    if (subscriptionResult.subscriptionStatus == SubscriptionStatus.PURCHASED) {
                        viewModel.planType = subscriptionResult.billingDetails?.products?.get(0)
                        viewModel.transactionID = subscriptionResult.billingDetails?.purchaseToken
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.SUBSCRIBE
                        )
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.subscriptionStatus.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ManageSubscriptionActivity,
                    success = {

                        val userData = sharedPreference.userData
                        userData?.data?.isSubscriptionApiHit = true
                        userData?.data?.purchaseToken = null
                        userData?.data?.planType = null
                        sharedPreference.userData = userData

                        when (viewModel.navigationFlow) {

                            AppConstant.SubscriptionStatus.SUBSCRIPTION_EXPIRED -> {
                                intent(
                                    destination = HomeActivity::class.java
                                )
                                finishAffinity()
                            }

                            AppConstant.SubscriptionStatus.SIGN_UP_FLOW -> {
                                setResult(Activity.RESULT_OK)
                                finish()
                                return@handleResponse
                            }

                            else -> {
                                viewModel.retryApiRequest(
                                    apiName = ApiConstant.SUBSCRIPTION_STATUS
                                )
                            }
                        }
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }

        lifecycleScope.launch {
            viewModel.getSubscriptionStatus.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ManageSubscriptionActivity,
                    success = { response ->
                        viewModel.productId = response.data?.planType
                        viewModel.adapter.currentList.forEachIndexed { index, subscriptionModel ->
                            if (response.data?.planType == subscriptionModel.productId) {
                                viewModel.adapter.currentList[index].selected = true
                                viewModel.adapter.notifyItemChanged(index)
                                viewModel.isSubscribed = true
                            }
                        }
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            if (viewModel.productId.isNullOrEmpty()) {
                showToast(
                    message = getString(R.string.please_select_subscription)
                )
                return@setOnClickListener
            }
            viewModel.subscriptionHelper?.initializingBillingClient(
                activity = this,
                productId = viewModel.productId ?: ""
            )
        }
    }

    private fun getCountryCode(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var countryCode: String?
        countryCode = telephonyManager.networkCountryIso
        if (countryCode.isNullOrEmpty()) {
            // If unable to get the country code from the network, fallback to device locale
            val locale: Locale = context.resources.configuration.locale
            countryCode = locale.country
        }
        return countryCode!!.uppercase(Locale.getDefault())
    }

}

