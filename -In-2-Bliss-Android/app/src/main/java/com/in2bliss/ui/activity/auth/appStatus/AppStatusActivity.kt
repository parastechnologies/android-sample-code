package com.in2bliss.ui.activity.auth.appStatus

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityAppStatusBinding
import com.in2bliss.ui.activity.auth.appStatus.freeTrailBottomSheet.FreeTrialBottomSheet
import com.in2bliss.ui.activity.home.profileManagement.manageSubscription.ManageSubscriptionActivity
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AppStatusActivity : BaseActivity<ActivityAppStatusBinding>(
    layout = R.layout.activity_app_status
) {

    override fun init() {
        setSubSubscriptionData()
        onClick()
    }

    private fun setSubSubscriptionData() {

        binding.tvSubscriptionDate.text =
            getString(R.string.your_subscription_will_start_on_march_21st).plus(" ")
                .plus(setCalenderData()).plus(".")

    }

    private fun setCalenderData(): String {
        val date = LocalDate.now()
        val plusDays = date.plusDays(7)
        val formatDate=plusDays.format(DateTimeFormatter.ofPattern("MMMM dd"))

        val last = formatDate.takeLast(1)
        var date2 = ""
        when (last) {
            "1" -> {
                date2 = formatDate.plus(" ").plus("st")
            }

            "2" -> {
                date2 = formatDate.plus(" ").plus("nd")
            }

            "3" -> {
                date2 = formatDate.plus(" ").plus("rd")
            }

            else -> {
                date2 = formatDate.plus(" ").plus("th")
            }
        }
        return date2
    }

    /**
     * handling on click [onClick]
     * */
    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnStartFreeTrial.setOnClickListener {
            val intent = Intent(this, ManageSubscriptionActivity::class.java)
            intent.putExtra(
                AppConstant.SUBSCRIPTION,
                AppConstant.SubscriptionStatus.SIGN_UP_FLOW.name
            )
            activityResult.launch(intent)
        }
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FreeTrialBottomSheet().show(supportFragmentManager, "Free trial bottom sheet")
            }
        }
}