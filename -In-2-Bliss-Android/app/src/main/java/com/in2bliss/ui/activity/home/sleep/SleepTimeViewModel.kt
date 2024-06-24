package com.in2bliss.ui.activity.home.sleep

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.timeDifference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SleepTimeViewModel @Inject constructor() : BaseViewModel() {

    val selectedHour = ObservableField("")
    val selectedMinute = ObservableField("")
    val wakeUpTime = ObservableField("7:00")
    val bedTime = ObservableField("7:00")
    val wakeUpAmPM = ObservableField("AM")
    val bedTimeAmPm = ObservableField("PM")
    var startHour: Int? = null
    var startMinute: Int? = null
    var endHour: Int? = null
    var endMinute: Int? = null

    override fun retryApiRequest(apiName: String) {

    }

    fun calculateTimeDifference() {
        timeDifference(
            startHour = startHour,
            endHour = endHour,
            startMinute = startMinute,
            endMinute = endMinute,
            timeDifference = { hour, minute ->
                selectedHour.set("${hour}hr")
                selectedMinute.set("${minute}min")
            }
        )
    }
}