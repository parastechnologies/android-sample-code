package com.in2bliss.ui.activity.home.reminder

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.GetNotificationStatusResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityReminderBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.ui.activity.home.journal.journalStreak.JournalStreakActivity
import com.in2bliss.ui.activity.home.quote.QuotesActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.convert24To12HourFormat
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.timePicker
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActivity : BaseActivity<ActivityReminderBinding>(
    layout = R.layout.activity_reminder
) {

    @Inject
    lateinit var sharedPreference: SharedPreference
    private val viewModel: ReminderViewModel by viewModels()

    override fun init() {
        binding.data = viewModel
        settingRecyclerView()
        gettingBundle()
        onClick()
        observer()
    }

    /**
     * observe data from view model [observer]
     * */
    private fun observer() {
        lifecycleScope.launch {
            viewModel.setReminderResponse.collectLatest {
                handleResponse(response = it, context = this@ReminderActivity, success = { _ ->
                    navigate()
                }, error = { message, apiName ->
                    alertDialogBox(message = message, retry = {
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    })
                })
            }
        }
        lifecycleScope.launch {
            viewModel.updateReminderResponse.collectLatest {
                handleResponse(response = it, context = this@ReminderActivity, success = { _ ->
                    navigate()
                }, error = { message, apiName ->
                    alertDialogBox(message = message, retry = {
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    })
                })
            }
        }
    }

    /**
     * get intent data [gettingBundle]
     * */
    private fun gettingBundle() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryName = when (categoryName) {
                AppConstant.HomeCategory.QUOTES.name -> AppConstant.HomeCategory.QUOTES
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                else -> null
            }
        }
        Log.d("ascsacsacs", "gettingBundle: ${viewModel.categoryName}")
        intent.getBooleanExtra(AppConstant.EDIT, false).let { edit ->
            viewModel.isEdit = edit
        }
        intent.getBooleanExtra(AppConstant.NOTIFICATION_ADD, false).let { noti ->
            viewModel.notificationAdd = noti
        }
        binding.journal.visibility(
            isVisible = viewModel.categoryName == AppConstant.HomeCategory.JOURNAL
        )
        if (viewModel.isEdit) {
            binding.tvSkip.gone()
        }
        if (viewModel.notificationAdd) {
            binding.tvSkip.gone()
        }

        when (viewModel.categoryName) {
            AppConstant.HomeCategory.JOURNAL -> {

                binding.tvSetYourReminder.setText(R.string.set_your_gratitude_reminders)
                binding.tvGratitudeJournalReminder.setText(R.string.increase_your_feelings_of_appreciation_with_prompts_and_reminders_to_think_about_what_you_are_grateful_for)

            }
            AppConstant.HomeCategory.QUOTES -> {
                binding.tvSetYourReminder.setText(R.string.set_your_reminders)
                binding.tvGratitudeJournalReminder.gone()
            }
            else -> {
                binding.tvJournalReminder.visible()
            }
        }


        try {
            Gson().fromJson(
                intent.getStringExtra(AppConstant.EDIT_GRATITUDE_BUNDLE),
                GetNotificationStatusResponse.Journal.Gratitude::class.java
            ).let {
                viewModel.bundleGratitude = it
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        /** Setting reminder data */
        try {
            Gson().fromJson(
                intent.getStringExtra(AppConstant.EDIT_BUNDLE),
                GetNotificationStatusResponse.Quote.Reminder::class.java
            ).let {
                viewModel.bundle = it
            }
            viewModel.setReminderData(
                context = this
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


    }

    /**
     * screen navigation [navigate]
     * */
    private fun navigate(isButtonClick: Boolean? = false) {
        when (viewModel.categoryName) {
            AppConstant.HomeCategory.JOURNAL -> {
                /** Changing the journal reminder status so it don't show again when coming to journal screen */
                if (viewModel.isEdit) {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (viewModel.notificationAdd) {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    val data = sharedPreference.userData
                    data?.data?.journalStatus = 1
                    data?.data?.journalReminderSkip = 1
                    sharedPreference.userData = data
                    val bundle = bundleOf(
                        AppConstant.CATEGORY_NAME to viewModel.categoryName?.name
                    )
                    intent(
                        destination = JournalStreakActivity::class.java, bundle = bundle
                    )
                    finish()
                }
            }

            AppConstant.HomeCategory.QUOTES -> {
                /** Changing the journal reminder status so it don't show again when coming to journal screen */
                if (viewModel.isEdit) {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (viewModel.notificationAdd) {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    val data = sharedPreference.userData
                    if (isButtonClick == true) {
                        data?.data?.quotesReminderSkip = 1
                        sharedPreference.userData = data
                    } else {
                        data?.data?.quoteStatus = 1
                        sharedPreference.userData = data
                    }
                    val bundle = bundleOf(
                        AppConstant.CATEGORY_NAME to viewModel.categoryName?.name
                    )
                    intent(
                        destination = QuotesActivity::class.java, bundle = bundle
                    )
                    finish()
                }
            }

            else -> {
                if (viewModel.isEdit) {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (viewModel.notificationAdd) {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    val userData = sharedPreference.userData
                    userData?.data?.profileStatus = 5
                    sharedPreference.userData = userData
                    intent(
                        destination = HomeActivity::class.java
                    )
                    finishAffinity()
                }
            }
        }
    }

    /**
     * handling on click [onClick]
     * */
    private fun onClick() {
        binding.btnContinue.setOnClickListener {
            selectedDate()
            when (viewModel.categoryName) {
                AppConstant.HomeCategory.QUOTES -> {
                    if (isValidate()) {
                        val apiName = if (viewModel.isEdit) {
                            ApiConstant.REMINDER_UPDATE
                        } else ApiConstant.REMINDER_SET
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    }

                }

                AppConstant.HomeCategory.JOURNAL -> {
                    viewModel.isNoReminder = binding.checkbox.isChecked
                    if (binding.checkbox.isChecked) {
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.JOURNAL_REMINDER
                        )
                        return@setOnClickListener
                    }
                    if (isValidate()) {
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.JOURNAL_REMINDER
                        )
                    }
                }

                else -> {
                    if (isValidate()) {
                        val apiName = if (viewModel.isEdit) {
                            ApiConstant.REMINDER_UPDATE
                        } else ApiConstant.REMINDER_SET
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    }
                }
            }
        }

        binding.clStartAt.setOnClickListener {
            timePicker(context = this, selectedTime = { hour, minute ->
                viewModel.startAt.set(
                    convert24To12HourFormat(
                        time = "$hour:$minute"
                    )
                )
            })
        }

        binding.clTime.setOnClickListener {
            timePicker(context = this, selectedTime = { hour, minute ->
                viewModel.time.set(
                    convert24To12HourFormat(
                        time = "$hour:$minute"
                    )
                )
            })
        }

        binding.clEndAt.setOnClickListener {
            timePicker(context = this, selectedTime = { hour, minute ->
                viewModel.endAt.set(
                    convert24To12HourFormat(
                        time = "$hour:$minute"
                    )
                )
            })
        }

        binding.tvSkip.setOnClickListener {
            navigate(true)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivTimeDecrease.setOnClickListener {
            if ((viewModel.numberOfTimes ?: 0) == 1) return@setOnClickListener
            viewModel.numberOfTimes =
                if (viewModel.numberOfTimes == null) 1 else viewModel.numberOfTimes?.minus(1)
            viewModel.howManyTimes.set("${viewModel.numberOfTimes} ${getString(R.string.times_day)}")
        }

        binding.ivTimeIncrease.setOnClickListener {
            if (viewModel.numberOfTimes == 20) return@setOnClickListener
            viewModel.numberOfTimes =
                if (viewModel.numberOfTimes == null) 1 else viewModel.numberOfTimes?.plus(1)
            viewModel.howManyTimes.set("${viewModel.numberOfTimes} ${getString(R.string.times_day)}")
        }
    }

    /**
     * select data  [selectedDate]
     * */

    private fun selectedDate() {
        var selectedDates = ""
        viewModel.gratitudeReminderAdapter.currentList.forEach { data ->
            if (data.isSelected) selectedDates += "${data.fullName},"
        }
        if (selectedDates.contains(",")) {
            selectedDates = selectedDates.substring(0, selectedDates.length - 1)
        }
        viewModel.gratitudeReminderDays = selectedDates
        if (viewModel.categoryName == AppConstant.HomeCategory.JOURNAL) {
            var journalDays = ""
            viewModel.journalReminderAdapter.currentList.forEach { data ->
                if (data.isSelected) journalDays += "${data.fullName},"
            }
            if (journalDays.contains(",")) {
                journalDays = journalDays.substring(0, journalDays.length - 1)
            }
            viewModel.journalReminderDays = journalDays
        }
    }

    private fun settingRecyclerView() {
        binding.rvReminder.layoutManager = GridLayoutManager(this, 7)
        binding.rvReminder.adapter = viewModel.gratitudeReminderAdapter
        binding.rvReminder.itemAnimator = null
        viewModel.gratitudeReminderAdapter.submitList(viewModel.getReminderDaysList())

        binding.rvJournalReminder.layoutManager = GridLayoutManager(this, 7)
        binding.rvJournalReminder.adapter = viewModel.journalReminderAdapter
        binding.rvJournalReminder.itemAnimator = null
        viewModel.journalReminderAdapter.submitList(viewModel.getReminderDaysList())
    }

    /**
     * check validation
     * */

    private fun isValidate(): Boolean {
        return when {
            (viewModel.time.get()
                ?.contains("--") == true && viewModel.categoryName == AppConstant.HomeCategory.JOURNAL) -> {
                showToast(getString(R.string.please_select_time_for_journal_reminder))
                false
            }

            (viewModel.journalReminderDays.isNullOrEmpty() && viewModel.categoryName == AppConstant.HomeCategory.JOURNAL) -> {
                showToast(
                    getString(R.string.please_select_at_least_one_day_for_journal_reminder)
                )
                false
            }

            (viewModel.numberOfTimes == null) -> {
                showToast(getString(R.string.please_set_reminder_per_day))
                false
            }

            (viewModel.startAt.get()?.contains("--") == true) -> {
                showToast(getString(R.string.please_select_start_at_time))
                false
            }

            (viewModel.endAt.get()?.contains("--") == true) -> {
                showToast(getString(R.string.please_select_end_at_time))
                false
            }

            (viewModel.gratitudeReminderDays.isNullOrEmpty()) -> {
                showToast(
                    if (viewModel.categoryName == AppConstant.HomeCategory.JOURNAL) {
                        getString(R.string.please_select_at_least_one_day_for_gratitude_reminder)
                    } else getString(R.string.please_select_at_least_one_day)
                )
                false
            }

            else -> true
        }
    }
}

