package com.mindbyromanzanoni.view.activity.setReminder

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.reminder.GetReminderData
import com.mindbyromanzanoni.databinding.ActivityReminderListBinding
import com.mindbyromanzanoni.databinding.ItemReminderListBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.convertDateFormatMessage
import com.mindbyromanzanoni.utils.getWeekName
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.showSuccessBarAlert
import com.mindbyromanzanoni.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReminderListActivity : BaseActivity<ActivityReminderListBinding>() {
    private val viewModel by viewModels<SetReminderViewModel>()
    override fun getLayoutRes() = R.layout.activity_reminder_list
    override fun initView() {
        setToolbar()
        binding.setAdapter()
        observeDataFromViewModal()
        viewModel.hitGetReminderListApi()
    }

    override fun viewModel() {}
    private fun setToolbar() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun ActivityReminderListBinding.setAdapter() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReminderListActivity)
            adapter = reminderListAdapter
        }
    }
    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModel.reminderListResponse.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            reminderListAdapter.submitList(data.data)
                        } else {
                            showErrorSnack(this@ReminderListActivity, data?.message ?: "")
                        }
                        if (data?.data.isNullOrEmpty()) {
                            binding.noDataFound.visible()
                        } else {
                            binding.noDataFound.gone()
                        }
                    }
                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(this@ReminderListActivity, msg)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deleteReminderResponse.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            showSuccessBarAlert(this@ReminderListActivity,getString(R.string.success),data.message ?: "Reminder Delete Successfully")
                            viewModel.hitGetReminderListApi()
                        }else{
                            showErrorSnack(this@ReminderListActivity, data?.message ?: "")
                        }
                    }
                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(this@ReminderListActivity, msg)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.showLoading.observe(this@ReminderListActivity) {
                if (it) {
                    MyProgressBar.showProgress(this@ReminderListActivity)
                } else {
                    MyProgressBar.hideProgress()
                }
            }
        }
    }

    private val reminderListAdapter =
        object : GenericAdapter<ItemReminderListBinding, GetReminderData>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_reminder_list
            }

            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onBindHolder(
                holder: ItemReminderListBinding,
                dataClass: GetReminderData,
                position: Int
            ) {
                  holder.tvTime.text = convertDateFormatMessage(dataClass.reminderDate)
                holder.tvLabel.text = dataClass.label
                holder.tvWeekDays.visibility =
                    if (dataClass.reminderTypeIds.isNullOrEmpty() || dataClass.reminderTypeIds == "0") View.GONE else View.VISIBLE
                holder.tvWeekDays.text = weekList(dataClass.reminderTypeIds ?: "")
                holder.ivDeleteReminder.setOnClickListener {
                    viewModel.hitDeleteReminderListApi(hashMapOf("ReminderId" to dataClass.reminderId))
                }
            }
        }

    private fun weekList(weekDaysIds: String): String {
        val weekdays = getWeekName()
        val weekdaysList = weekDaysIds.split(",")
        var weekDaysName = ""
        for (i in weekdaysList.indices) {
            for (j in weekdays.indices) {
                if (weekdaysList[i].trim() == weekdays[j].id.toString().trim()) {
                    weekDaysName = if (weekDaysName.isEmpty().not()
                    ) "$weekDaysName,${weekdays[j].shortName}" else weekdays[j].shortName
                    break
                }
            }
        }
        return weekDaysName
    }
}