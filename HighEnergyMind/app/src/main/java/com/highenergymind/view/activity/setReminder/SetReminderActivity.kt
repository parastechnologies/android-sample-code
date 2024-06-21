package com.highenergymind.view.activity.setReminder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.data.CheckModel
import com.highenergymind.data.Data
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.ActivitySetReminderBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.invisible
import com.highenergymind.utils.showToast
import com.highenergymind.utils.toDateFormat
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.adapter.WeekDayAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetReminderActivity : BaseActivity<ActivitySetReminderBinding>() {
    val viewModel by viewModels<SetReminderViewModel>()
    val adapter by lazy {
        WeekDayAdapter()
    }
    private var startTimeIndex = 0
    private var endTimeIndex = 0
    private var manyTimesIndex = 1

    override fun getLayoutRes(): Int {
        return R.layout.activity_set_reminder
    }

    override fun initView() {
        fullScreenStatusBar()
        setAdapter()
        setCollectors()
        getBundleData()
        clicks()
        viewModel.getReminderApi()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                getReminderResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        response.data?.let { it1 -> setDataOnScreen(it1) }
                    }, isShowToast = false)
                }
            }
            lifecycleScope.launch {
                setReminderResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        showToast(response.message)
                        val bundle = Bundle()
                        if (intent.getIntExtra(AppConstant.SCREEN_FROM, 0) == R.id.profile) {
                            bundle.putInt(AppConstant.SCREEN_FROM, R.id.profile)
                        }
                        intentComponent(HomeActivity::class.java, bundle)
                        finishAffinity()

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

    private fun setDataOnScreen(userResponse: Data) {
        binding.apply {
            userResponse.notifyReminder?.let { data ->

                binding.switch1.isChecked = userResponse.isNotification == 1

                manyTimesIndex = data.timesPerDay
                startTimeIndex = germanClockArray.indexOf(data.startTime)
                endTimeIndex = germanClockArray.indexOf(data.endTime)
                data.days.forEach { day ->
                    if (day.isNotEmpty()) {
                        val itemIndex = adapter.currentList.indexOf(CheckModel(day))
                        adapter.currentList[itemIndex].isChecked = true
                        adapter.notifyItemChanged(itemIndex)
                    }
                }
                setStartTimeValue()
                setEndTimeValue()
                setManyTimesValue()
            }
        }
    }

    private fun getBundleData() {

        binding.apply {
            if (!intent.hasExtra(AppConstant.IS_FROM_SIGN_UP)) {
                enableDisableView()
                tvSkip.gone()
            } else {
                switch1.invisible()
            }

            setStartTimeValue()
            setEndTimeValue()
            setManyTimesValue()

        }
    }

    private fun setAdapter() {
        binding.apply {
            rvWeekDay.adapter = adapter
            adapter.submitList(arrayWeek)
        }
    }

    private fun clicks() {
        binding.apply {

            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                enableDisableView()

            }
            ivBack.setOnClickListener {
                finish()
            }
            tvSkip.setOnClickListener {
                intentComponent(HomeActivity::class.java)
                finishAffinity()
            }

            ivMinStartTime.setOnClickListener {
                startTimeIndex -= 1
                setStartTimeValue(isPlus = false)
            }
            ivPlusStartTime.setOnClickListener {
                startTimeIndex += 1
                setStartTimeValue()
            }
            ivMinEndTime.setOnClickListener {
                endTimeIndex -= 1
                setEndTimeValue(isPlus = false)
            }
            ivPlusEndTime.setOnClickListener {
                endTimeIndex += 1
                setEndTimeValue()
            }
            ivMinTimes.setOnClickListener {
                if (manyTimesIndex > 1) {
                    manyTimesIndex -= 1
                    setManyTimesValue()
                }
            }
            ivPlusTimes.setOnClickListener {
                manyTimesIndex += 1
                setManyTimesValue()
            }

            btnAdd.setOnClickListener {

                if (adapter.currentList.none { it.isChecked } && switch1.isChecked) {
                    showToast(getString(R.string.please_select_a_day))
                } else {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.START_TIME] = germanClockArray[startTimeIndex]
                        map[ApiConstant.END_TIME] = germanClockArray[endTimeIndex]
                        map[ApiConstant.PER_DAY] = manyTimesIndex
                        map[ApiConstant.NOTIFY_REMINDER] = switch1.isChecked
                        map[ApiConstant.DAYS] =
                            adapter.currentList.filter { it.isChecked }
                                .joinToString(",") { it.name }
                        setReminderApi()
                    }
                }
            }
        }
    }

    private fun enableDisableView() {
        binding.apply {
            if (switch1.isChecked) {
                cvCard.alpha = 1f
                vDisable.gone()
            } else {
                cvCard.alpha = 0.6f
                vDisable.visible()
            }
        }
    }

    private fun setManyTimesValue() {
        binding.apply {
            tvTimesValue.text = "$manyTimesIndex ${getString(R.string.times_day)}"
        }
    }

    private fun setStartTimeValue(isPlus: Boolean = true) {
        binding.apply {
            try {
                tvStartTimeValue.text =
                   if (ApplicationClass.isEnglishSelected.not()) germanClockArray[startTimeIndex] else germanClockArray[startTimeIndex].toDateFormat("HH:mm", "hh:mm a")
            } catch (e: Exception) {
                startTimeIndex = if (isPlus) {
                    0
                } else {
                    germanClockArray.lastIndex
                }
                tvStartTimeValue.text =
                    if (ApplicationClass.isEnglishSelected.not()) germanClockArray[startTimeIndex] else  germanClockArray[startTimeIndex].toDateFormat("HH:mm", "hh:mm a")
            }
        }
    }

    private fun setEndTimeValue(isPlus: Boolean = true) {
        binding.apply {
            try {
                tvEndTimeValue.text =
                    if (ApplicationClass.isEnglishSelected.not()) germanClockArray[endTimeIndex] else  germanClockArray[endTimeIndex].toDateFormat("HH:mm", "hh:mm a")
            } catch (e: Exception) {
                endTimeIndex = if (isPlus) {
                    0
                } else {
                    germanClockArray.lastIndex
                }
                tvEndTimeValue.text =
                    if (ApplicationClass.isEnglishSelected.not()) germanClockArray[endTimeIndex] else germanClockArray[endTimeIndex].toDateFormat("HH:mm", "hh:mm a")
            }
        }
    }

    private val arrayWeek = mutableListOf(
        CheckModel("mon"),
        CheckModel("tue"),
        CheckModel("wed"),
        CheckModel("thu"),
        CheckModel("fri"),
        CheckModel("sat"),
        CheckModel("sun"),
    )
    private val germanClockArray = arrayOf(
        "00:00",
        "00:30",
        "01:00",
        "01:30",
        "02:00",
        "02:30",
        "03:00",
        "03:30",
        "04:00",
        "04:30",
        "05:00",
        "05:30",
        "06:00",
        "06:30",
        "07:00",
        "07:30",
        "08:00",
        "08:30",
        "09:00",
        "09:30",
        "10:00",
        "10:30",
        "11:00",
        "11:30",
        "12:00",
        "12:30",
        "13:00",
        "13:30",
        "14:00",
        "14:30",
        "15:00",
        "15:30",
        "16:00",
        "16:30",
        "17:00",
        "17:30",
        "18:00",
        "18:30",
        "19:00",
        "19:30",
        "20:00",
        "20:30",
        "21:00",
        "21:30",
        "22:00",
        "22:30",
        "23:00",
        "23:30"
    )

}