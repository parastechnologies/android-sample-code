package com.in2bliss.ui.activity.home.fragment.meditationTracker.dashboard

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.MeditationTrackerStreakResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.getDatesForCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class DashboardVM @Inject constructor(private val apiHelperInterface: ApiHelperInterface) :
    BaseViewModel() {

    val currentStreak = ObservableField("0")
    val totalEntries = ObservableField("0")
    val totalMeditationTime = ObservableField("0")
    val meditationAverage = ObservableField("")

    var selectedPositionListener: ((position: Int) -> Unit)? = null
    val horizontalCalendar by lazy {
        HorizontalCalendarAdapter()
    }

    private val mutableMeditationStreakResponse by lazy {
        MutableSharedFlow<Resource<MeditationTrackerStreakResponse>>()
    }
    val meditationStreakReasonsResponse by lazy { mutableMeditationStreakResponse.asSharedFlow() }

    fun setCalendarData() {
        getDatesForCalendar(
            incrementMonth = null,
            calendarData = { dateList, _, _ ->
                horizontalCalendar.submitList(dateList) {

                    /** Current selected date scroll listener  */
                    dateList.forEachIndexed { index, data ->

                        val isCurrentDate = getCurrentDate() == "${
                            String.format("%02d", data.year)
                        }-${String.format("%02d", data.month)}-${String.format("%02d", data.date)}"

                        if (isCurrentDate) selectedPositionListener?.invoke(index)
                    }
                }
            }
        )
    }

    /**
     * Getting Weekly meditation streak
     * */
    private fun meditationStreak() {
        networkCallIo {
            mutableMeditationStreakResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.meditationStreak()
                },
                apiName = ApiConstant.MEDITATION_STREAK
            )
            mutableMeditationStreakResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.MEDITATION_STREAK -> meditationStreak()
        }
    }
}