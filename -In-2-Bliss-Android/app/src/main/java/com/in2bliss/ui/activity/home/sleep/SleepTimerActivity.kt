package com.in2bliss.ui.activity.home.sleep

import androidx.activity.viewModels
import androidx.core.os.bundleOf
import com.google.common.base.Ascii.FF
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivitySleepTimerBinding
import com.in2bliss.ui.activity.home.affirmationExplore.AffirmationExploreActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.intent
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.timerangepicker.TimeRangePicker

@AndroidEntryPoint
class SleepTimerActivity : BaseActivity<ActivitySleepTimerBinding>(
    layout = R.layout.activity_sleep_timer

) {
    private val viewModel: SleepTimeViewModel by viewModels()

    override fun init() {
        binding.data = viewModel
        settingDefaultClockTime()
        onClick()
    }

    /**
     * Setting default clock time
     * */
    private fun settingDefaultClockTime() {
//        binding.clock.isStartEnabled = true
//        binding.clock.isEndEnabled = true
//        viewModel.startHour = binding.clock.startHours.toString().substringBefore(".").toInt()
//        viewModel.startMinute = binding.clock.startHours.toString().substringAfter(".").toInt()
//        viewModel.endHour = binding.clock.endHours.toString().substringBefore(".").toInt()
//        viewModel.endMinute = binding.clock.endHours.toString().substringAfter(".").toInt()
        viewModel.calculateTimeDifference()
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.GUIDED_SLEEP.name
            )
            intent(
                destination = AffirmationExploreActivity::class.java,
                bundle = bundle
            )
            finish()
        }

        binding.clock.setOnTimeChangeListener(object : TimeRangePicker.OnTimeChangeListener {
            override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
                viewModel.wakeUpTime.set(
                    formatDate(
                        startTime.toString(),
                        "HH:mm",
                        "hh:mm a"
                    )
                )
            }

            override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
                viewModel.bedTime.set(
                    formatDate(
                        endTime.toString(),
                        "HH:mm",
                        "hh:mm a"
                    )
                )
            }

            override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
                val list = duration.toString().split(
                    ":"
                )
                viewModel.selectedHour.set(
                    list[0].plus("hr")
                )
                viewModel.selectedMinute.set(
                    list[1].plus("min")
                )
            }
        })
    }
}