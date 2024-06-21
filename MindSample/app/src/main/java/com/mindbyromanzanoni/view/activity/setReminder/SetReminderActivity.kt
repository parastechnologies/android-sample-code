package com.mindbyromanzanoni.view.activity.setReminder

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivitySetReminderBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.convertTimeLocalToUtc
import com.mindbyromanzanoni.utils.convertTimeLocalToUtcUnitedState
import com.mindbyromanzanoni.utils.customDateFormat
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.getWeekName
import com.mindbyromanzanoni.utils.newIntent
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.showTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class SetReminderActivity : BaseActivity<ActivitySetReminderBinding>() {
    private val setReminderViewModel by viewModels<SetReminderViewModel>()
    override fun getLayoutRes() = R.layout.activity_set_reminder
    private var weekDaysIds = "0"
    private var weekDaysName = "0"
    override fun initView() {
        setToolbar()
        onClickListener()
        observeDataFromViewModal()
    }

    override fun viewModel() {}
    private fun setToolbar() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun onClickListener() {
        binding.apply {
            btnSave.setOnClickListener {
                val model = SetReminderModel(
                    time = etDates.text.toString(),
                    repeat = weekDaysIds,
                    label = etLabel.text.toString().trim())
                if (setReminderViewModel.validations(this@SetReminderActivity, model)) {
                     val hashmap = hashMapOf<String, Any?>("ReminderDate" to convertTimeLocalToUtc(model.time).uppercase(), "Label" to model.label, "ReminderTypeIds" to model.repeat)
                    setReminderViewModel.hitReminderApi(hashmap)
                }
            }
        }
        binding.repeatLayout.setOnClickListener {
            val intent = newIntent<RepeatAlarmActivity>(this)
            intent.putExtra("value", weekDaysIds)
            launcher.launch(intent)
        }
        binding.etDates.setOnClickListener {
            showTimePicker(
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false
            ) { hr, mint ->
                val dateTime = "$hr:$mint"
                val getTime = customDateFormat(dateTime, "HH:mm", "hh:mm a").uppercase()
                binding.etDates.text = getTime
            }
        }

        binding.reminderList.setOnClickListener {
            val intent = newIntent<ReminderListActivity>(this)
            startActivity(intent)
        }
        binding.btnCancel.setOnClickListener {
             finish()
        }
    }
    private var launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val value = result.data?.getStringExtra("value")
                weekDaysIds = if (value.isNullOrEmpty()) "0" else value
                weekDaysName = ""
                val weekdays = getWeekName()
                val weekdaysList = weekDaysIds.split(",")
                for (i in weekdaysList.indices) {
                    for (j in weekdays.indices) {
                        if (weekdaysList[i].trim() == weekdays[j].id.toString().trim()) {
                            weekDaysName = if (weekDaysName.isEmpty().not()) "$weekDaysName,${weekdays[j].shortName}" else weekdays[j].shortName
                            break
                        }
                    }
                }
                binding.etSubject.text = weekDaysName
            }
        }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            setReminderViewModel.repeatReminderResponse.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            finishActivity()
                        }
                        showErrorSnack(this@SetReminderActivity, data?.message ?: "")
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(this@SetReminderActivity, msg)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            setReminderViewModel.showLoading.observe(this@SetReminderActivity) {
                if (it) {
                    MyProgressBar.showProgress(this@SetReminderActivity)
                } else {
                    MyProgressBar.hideProgress()
                }
            }
        }
    }
}