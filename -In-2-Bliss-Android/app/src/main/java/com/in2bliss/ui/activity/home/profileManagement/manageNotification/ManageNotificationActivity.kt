package com.in2bliss.ui.activity.home.profileManagement.manageNotification

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.GetNotificationStatusResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityManageNotificationBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.reminder.ReminderActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageNotificationActivity : BaseActivity<ActivityManageNotificationBinding>(
    layout = R.layout.activity_manage_notification
) {

    private val viewModel: ManageNotificationVM by viewModels()
    var bundle = GetNotificationStatusResponse.Quote.Reminder()
    private var bundleGratitude = GetNotificationStatusResponse.Journal.Gratitude()
    private var responseNotification = GetNotificationStatusResponse()

    override fun init() {
        onClick()
        viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS)
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.getNotificationResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ManageNotificationActivity,
                    success =
                    { response ->
                        responseNotification = response
                        binding.tvDescription.text =
                            getString(R.string.notification).plus(" ")
                                .plus(((response.journal?.gratitude?.interval) ?: 0.toString()))
                                .plus(" ")
                                .plus(getString(R.string.times_a_day))

                        binding.tvDescriptionAffirmation.text =
                            getString(R.string.notification).plus(" ")
                                .plus(((response.affirmation?.reminder?.interval) ?: 0.toString()))
                                .plus(" ")
                                .plus(getString(R.string.times_a_day))

                        binding.tvDescriptionQuotes.text =
                            getString(R.string.notification).plus(" ")
                                .plus(((response.quote?.reminder?.interval) ?: 0.toString()))
                                .plus(" ")
                                .plus(getString(R.string.times_a_day))

                        if (response.journal?.notificationStatus == 1) {
                            binding.switchNextTimeJournalOnOff.isChecked = true
                        }
                        if (response.affirmation?.affirmationStatus == 1) {
                            binding.switchNextTimeAffirmationOnOff.isChecked = true
                        }
                        if (response.quote?.affirmationStatus == 1) {
                            binding.switchNextTimeQuotesOnOff.isChecked = true
                        }
                        initListener()

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
            viewModel.setNotificationResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ManageNotificationActivity,
                    success = { response ->
                        showToast(response.message.toString())
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
        binding.apply {
            toolBar.tvTitle.text = getString(R.string.manage_notifications)
            toolBar.ivBack.setOnClickListener { finish() }
            clJournal.setOnClickListener {
                if (responseNotification.journal?.gratitude?.interval == null && responseNotification.journal?.gratitude?.interval == 0) {
                    val intent =
                        Intent(this@ManageNotificationActivity, ReminderActivity::class.java)
                    intent.putExtra(
                        AppConstant.CATEGORY_NAME,
                        AppConstant.HomeCategory.GUIDED_AFFIRMATION.name
                    )
                    intent.putExtra(AppConstant.NOTIFICATION_ADD, true)
                    callFormUpDateReminder.launch(intent)
                } else {
                    bundle = GetNotificationStatusResponse.Quote.Reminder(
                        id = responseNotification.journal?.reminder?.id,
                        days = responseNotification.journal?.reminder?.days,
                        startTime = responseNotification.journal?.reminder?.startTime,
                        endTime = responseNotification.journal?.reminder?.endTime,
                        interval = responseNotification.journal?.reminder?.interval,
                        type = responseNotification.journal?.reminder?.type
                    )
                    bundleGratitude = GetNotificationStatusResponse.Journal.Gratitude(
                        id = responseNotification.journal?.gratitude?.id,
                        days = responseNotification.journal?.gratitude?.days,
                        startTime = responseNotification.journal?.gratitude?.startTime,
                        endTime = responseNotification.journal?.gratitude?.endTime,
                        interval = responseNotification.journal?.gratitude?.interval,
                        type = responseNotification.journal?.gratitude?.type
                    )

                    val intent =
                        Intent(this@ManageNotificationActivity, ReminderActivity::class.java)
                    intent.putExtra(
                        AppConstant.CATEGORY_NAME,
                        AppConstant.HomeCategory.JOURNAL.name
                    )
                    intent.putExtra(AppConstant.EDIT, true)
                    intent.putExtra(AppConstant.EDIT_BUNDLE, Gson().toJson(bundle))
                    intent.putExtra(
                        AppConstant.EDIT_GRATITUDE_BUNDLE,
                        Gson().toJson(bundleGratitude)
                    )
                    callFormUpDateReminder.launch(intent)
                }

            }
            clAffirmation.setOnClickListener {

                if (responseNotification.affirmation?.reminder == null) {
                    val intent =
                        Intent(this@ManageNotificationActivity, ReminderActivity::class.java)
                    intent.putExtra(
                        AppConstant.CATEGORY_NAME,
                        AppConstant.HomeCategory.GUIDED_AFFIRMATION.name
                    )
                    intent.putExtra(AppConstant.NOTIFICATION_ADD, true)
                    callFormUpDateReminder.launch(intent)
                } else {
                    bundle = GetNotificationStatusResponse.Quote.Reminder(
                        id = responseNotification.affirmation?.reminder?.id,
                        days = responseNotification.affirmation?.reminder?.days,
                        startTime = responseNotification.affirmation?.reminder?.startTime,
                        endTime = responseNotification.affirmation?.reminder?.endTime,
                        interval = responseNotification.affirmation?.reminder?.interval,
                        type = responseNotification.affirmation?.reminder?.type
                    )

                    val intent =
                        Intent(this@ManageNotificationActivity, ReminderActivity::class.java)
                    intent.putExtra(
                        AppConstant.CATEGORY_NAME,
                        AppConstant.HomeCategory.GUIDED_AFFIRMATION.name
                    )
                    intent.putExtra(AppConstant.EDIT, true)
                    intent.putExtra(AppConstant.EDIT_BUNDLE, Gson().toJson(bundle))
                    callFormUpDateReminder.launch(intent)
                }

            }
            clQuotes.setOnClickListener {
                if (responseNotification.quote?.reminder == null) {
                    val intent =
                        Intent(this@ManageNotificationActivity, ReminderActivity::class.java)
                    intent.putExtra(AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.QUOTES.name)
                    intent.putExtra(AppConstant.NOTIFICATION_ADD, true)
                    callFormUpDateReminder.launch(intent)
                } else {
                    bundle = GetNotificationStatusResponse.Quote.Reminder(
                        id = responseNotification.quote?.reminder?.id,
                        days = responseNotification.quote?.reminder?.days,
                        startTime = responseNotification.quote?.reminder?.startTime,
                        endTime = responseNotification.quote?.reminder?.endTime,
                        interval = responseNotification.quote?.reminder?.interval,
                        type = responseNotification.quote?.reminder?.type
                    )

                    val intent =
                        Intent(this@ManageNotificationActivity, ReminderActivity::class.java)
                    intent.putExtra(AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.QUOTES.name)
                    intent.putExtra(AppConstant.EDIT, true)
                    intent.putExtra(AppConstant.EDIT_BUNDLE, Gson().toJson(bundle))
                    callFormUpDateReminder.launch(intent)
                }

            }
        }
    }

    private fun initListener() {
        binding.apply {
            switchNextTimeJournalOnOff.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.status =
                        ApiConstant.NotificationOnOff.NOTIFICATION_ON.value.toString()
                    viewModel.type = ApiConstant.Types.AFFIRMATION.value.toString()
                    viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS_SET)

                } else {
                    viewModel.status =
                        ApiConstant.NotificationOnOff.NOTIFICATION_OFF.value.toString()
                    viewModel.type = ApiConstant.Types.AFFIRMATION.value.toString()
                    viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS_SET)
                }
            }
            switchNextTimeAffirmationOnOff.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.status =
                        ApiConstant.NotificationOnOff.NOTIFICATION_ON.value.toString()
                    viewModel.type = ApiConstant.Types.JOURNAL.value.toString()
                    viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS_SET)

                } else {
                    viewModel.status =
                        ApiConstant.NotificationOnOff.NOTIFICATION_OFF.value.toString()
                    viewModel.type = ApiConstant.Types.JOURNAL.value.toString()
                    viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS_SET)
                }
            }
            switchNextTimeQuotesOnOff.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.status =
                        ApiConstant.NotificationOnOff.NOTIFICATION_ON.value.toString()
                    viewModel.type = ApiConstant.Types.QUOTES.value.toString()
                    viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS_SET)

                } else {
                    viewModel.status =
                        ApiConstant.NotificationOnOff.NOTIFICATION_OFF.value.toString()
                    viewModel.type = ApiConstant.Types.QUOTES.value.toString()
                    viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS_SET)
                }
            }
        }
    }

    private val callFormUpDateReminder =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.retryApiRequest(ApiConstant.NOTIFICATION_STATUS)
            }
        }
}
