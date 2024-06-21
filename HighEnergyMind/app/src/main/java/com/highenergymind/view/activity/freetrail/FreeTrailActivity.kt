package com.highenergymind.view.activity.freetrail

import android.app.Activity
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.databinding.ActivityFreeTrailBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.disclaimer.DisclaimerActivity
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.activity.privacypolicy.PrivacyPolicyActivity
import com.highenergymind.view.activity.setReminder.SetReminderActivity
import com.highenergymind.view.activity.termsconditions.TermsConditionsActivity
import com.highenergymind.view.sheet.congratulations.CongratulationSheet
import com.highenergymind.view.sheet.redeemCode.RedeemCodeSheet
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class FreeTrailActivity : BaseActivity<ActivityFreeTrailBinding>() {
    val viewModel by viewModels<FreeTrialViewModel>()
    private lateinit var offering: Offering
    private var selectedPackage: Package? = null
    private var isPaymentOpen: Boolean = false
    override fun getLayoutRes(): Int {
        return R.layout.activity_free_trail
    }


    override fun initView() {
        fullScreenStatusBar()
        onClick()
        setCollectors()
        setClickableSpan()
        setChargeDate()
        binding.groupDivider.gone()
        getOfferings()
    }

    private fun setChargeDate() {
        Calendar.getInstance().let { c ->
            c.add(Calendar.DATE, 7)
            val format = SimpleDateFormat("MMMM dd", Locale.getDefault())
            val time = format.format(c.time)
            binding.tvCancelAt.text =
                getString(R.string.you_ll_be_charged_on_january_29_cancel_anytime_before, time)
        }

    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                addSubResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        congratulationSheet()
                    })
                }
            }
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
        }
    }


    private fun getOfferings() {
        progressDialog(true)
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                progressDialog(false)
                /* Optional error handling */
            },
            onSuccess = { offerings ->
                progressDialog(false)
                offerings.current?.let {
                    offering = it
                    setData()
                }
            })
    }

    private fun setData() {
        binding.apply {
            selectedPackage = offering.annual
            groupDivider.visible()
            monthlyPlanTV.text = getString(
                R.string._7_day_free_trial_nthen_9_99_month,
                offering.monthly?.product?.price?.formatted
            )
            yearlyPlanTV.text = getString(
                R.string._7_day_free_trial_n_then_79_99_year,
                offering.annual?.product?.price?.formatted
            )
        }
    }

    private fun onClick() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }

            ivClose.setOnClickListener {
                intentComponent(HomeActivity::class.java, intent.extras)
            }
        }

        binding.redemCodeTV.setOnClickListener {
            RedeemCodeSheet().show(supportFragmentManager, "")
        }
        binding.startTrailBtn.setOnClickListener {
            startPaymentRevenue()
        }

        binding.monthlyPlanCL.setOnClickListener {
            selectedPackage = offering.monthly
            setSelected(it)
        }

        binding.yearlyPlanCL.setOnClickListener {
            selectedPackage = offering.annual
            setSelected(it)
        }

    }

    override fun onResume() {
        super.onResume()

        if (isPaymentOpen) {
            progressDialog(true)
        }
    }

    private fun startPaymentRevenue() {
        isPaymentOpen = true

        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(
                this, selectedPackage!!
            ).build(),
            onError = { error, userCancelled ->
                isPaymentOpen = false

                when (error.code) {
                    PurchasesErrorCode.ProductAlreadyPurchasedError -> {
                        congratulationSheet()
                    }

                    else -> {

                    }
                }

                progressDialog(false)

            },
            onSuccess = { storeTransaction, customerInfo ->
                Log.e("storeTransaction", "${Gson().toJson(storeTransaction)}")
                Log.e("customerInfo", "${Gson().toJson(customerInfo)}")


                isPaymentOpen = false
                progressDialog(false)
                if (customerInfo.entitlements["HEM Premium"]?.isActive == true) {
                    // Unlock that great "pro" content
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.PAYMENT_TOKEN] = storeTransaction?.orderId ?: ""
                        map[ApiConstant.PRICE] = selectedPackage?.product?.price?.formatted ?: ""
                        map[ApiConstant.TENURE] =
                            if (customerInfo.entitlements.active["HEM Premium"]?.productIdentifier == AppConstant.SUBSCRIPTION.YEARLY.value) AppConstant.YEARLY else AppConstant.MONTHLY
                        val format = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ) /* Mon May 20 16:31:38 GMT+05:30 2024 */
                        val date =
                            format.format(customerInfo.entitlements.active["HEM Premium"]?.expirationDate)

                        map[ApiConstant.LAST_DATE] = date

                        addSubApi()
                    }
                }
            })
    }

    private fun congratulationSheet() {
        val userData = viewModel.sharedPrefs.getUserData()
        userData?.isSubscription = "purchased"
        viewModel.sharedPrefs.saveUserData(userData!!)
        val congsDialog = CongratulationSheet()
        congsDialog.isCancelable = false
        congsDialog.callBack = {
            if (intent.hasExtra(AppConstant.SCREEN_FROM) && intent.getIntExtra(
                    AppConstant.SCREEN_FROM,
                    0
                ) == R.id.musicCV
            ) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                intentComponent(SetReminderActivity::class.java, intent.extras)
            }

        }
        supportFragmentManager.let { congsDialog.show(it, "ModalBottomSheetDialog.TAG") }
    }


    private fun setSelected(view: View?) {
        binding.apply {
            val unCheckedBgPlan = ContextCompat.getDrawable(
                this@FreeTrailActivity,
                R.drawable.drawable_un_checked_plan
            )
            val checkedBgPlan =
                ContextCompat.getDrawable(this@FreeTrailActivity, R.drawable.drawable_checked_plan)

            val unCheckedSmall = ContextCompat.getDrawable(
                this@FreeTrailActivity,
                R.drawable.drawable_un_checked_small
            )
            val checkedSmall =
                ContextCompat.getDrawable(this@FreeTrailActivity, R.drawable.drawable_checked_small)
            val unCheckedColorPlan =
                ContextCompat.getColorStateList(this@FreeTrailActivity, R.color.content_color)
            val unCheckedColorPlanSmall =
                ContextCompat.getColorStateList(this@FreeTrailActivity, R.color.bg_color_1)

            val checkedColor =
                ContextCompat.getColorStateList(this@FreeTrailActivity, R.color.white)

            monthlyPlanTV.setTextColor(unCheckedColorPlan)
            yearlyPlanTV.setTextColor(unCheckedColorPlan)

            tvMonthly.setTextColor(unCheckedColorPlanSmall)
            tvYear.setTextColor(unCheckedColorPlanSmall)

            tvMonthly.background = unCheckedSmall
            tvYear.background = unCheckedSmall

            monthlyPlanCL.background = unCheckedBgPlan
            yearlyPlanCL.background = unCheckedBgPlan
            when (view) {
                monthlyPlanCL -> {
                    monthlyPlanCL.background = checkedBgPlan
                    tvMonthly.background = checkedSmall
                    monthlyPlanTV.setTextColor(checkedColor)
                    tvMonthly.setTextColor(checkedColor)
                }

                yearlyPlanCL -> {
                    yearlyPlanCL.background = checkedBgPlan
                    tvYear.background = checkedSmall
                    yearlyPlanTV.setTextColor(checkedColor)
                    tvYear.setTextColor(checkedColor)
                }
            }
        }
    }

    private fun setClickableSpan() {
        val stringBuilder = SpannableStringBuilder()
        stringBuilder.append(getString(R.string.terms_and_conditions)).append(" | ")
            .append(getString(R.string.privacy_policy)).append(" | ")
            .append(getString(R.string.disclaimer)).append(" | ")
            .append(getString(R.string.restore_purchase))
        stringBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    intentComponent(TermsConditionsActivity::class.java)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@FreeTrailActivity, R.color.bg_color_1)
                }
            },
            0,
            getString(R.string.terms_and_conditions).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        stringBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    intentComponent(PrivacyPolicyActivity::class.java)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@FreeTrailActivity, R.color.bg_color_1)
                }
            },
            stringBuilder.indexOf(getString(R.string.privacy_policy)),
            stringBuilder.indexOf(getString(R.string.privacy_policy)) + getString(R.string.privacy_policy).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        stringBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    intentComponent(DisclaimerActivity::class.java)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@FreeTrailActivity, R.color.bg_color_1)
                }
            },
            stringBuilder.indexOf(getString(R.string.disclaimer)),
            stringBuilder.indexOf(getString(R.string.disclaimer)) + getString(R.string.disclaimer).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.contentTV.text = stringBuilder
        binding.contentTV.movementMethod = LinkMovementMethod.getInstance()
        binding.contentTV.highlightColor = Color.TRANSPARENT

    }


}