package com.highenergymind.view.activity.subscription

import android.content.Intent
import android.icu.util.Calendar
import androidx.core.net.toUri
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.Data
import com.highenergymind.databinding.ActivitySubscriptionBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.toDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SubscriptionPlanActivity : BaseActivity<ActivitySubscriptionBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.activity_subscription
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolBar()
        checkSubscription()
        clicks()
    }

    private fun clicks() {
        binding.apply {
            cancelBtn.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/account/subscriptions".toUri()
                )
                startActivity(intent)
            }
        }
    }

    private fun setToolBar() {
        binding.customTool.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.subscription_plan)
        }
    }

    private fun checkSubscription() {
        val userData = sharedPrefs.getUserData()
        binding.apply {
            tvPlanType.text =
                when (userData?.isSubscription) {
                    AppConstant.MONTHLY -> getString(R.string.monthly)
                    AppConstant.SEVEN_DAYS -> getString(R.string._7_days_free_trial)
                    else -> getString(R.string.yearly)
                }
            dayLeftTV.text = getDayLeft(userData)
            tvDateExpire.text =
                userData?.subscriptionLastData?.toDateFormat("yyyy-MM-dd HH:mm:ss", "MMMM dd, yyyy")
            tvAmountYearly.text =if ((userData?.isSubscription)==AppConstant.SEVEN_DAYS) getString(R.string.free_trial) else "${userData?.subscriptionPlan}/${
                if (userData?.isSubscription == AppConstant.MONTHLY) getString(R.string.month) else "${getString(R.string.year)}"
            }"
        }

    }

    private fun getDayLeft(userData: Data?): String {
        val calendarMill = Calendar.getInstance().timeInMillis
        val expireMill = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).parse(userData?.subscriptionLastData).time
        val diff = calendarMill - expireMill
        return TimeUnit.MILLISECONDS.toDays(diff).toString() + " ${getString(R.string.day_left)}"

    }

}