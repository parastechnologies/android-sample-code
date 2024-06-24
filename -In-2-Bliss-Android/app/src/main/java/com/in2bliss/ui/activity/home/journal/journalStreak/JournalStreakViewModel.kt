package com.in2bliss.ui.activity.home.journal.journalStreak

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.CalendarDate
import com.in2bliss.data.model.journalStreak.JournalStreak
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getDatesForCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class JournalStreakViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val currentStreak = ObservableField("00 Days")
    val totalEntries = ObservableField("00 Days")
    val longestWeek = ObservableField("00 Days")
    val date = ObservableField("")
    val daysOff = ObservableField("")

    private val daysList = arrayListOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    var categoryName: AppConstant.HomeCategory? = null
    private var incrementMonth: Int = 0
    private var selectedYear: Int? = null
    private var selectedMonth: Int? = null
    var selectedDate: String? = null
    var position: Int? = null
    var journalData: String? = null

    val adapter by lazy {
        CalendarAdapter()
    }

    private val mutableJournalStreak by lazy {
        MutableSharedFlow<Resource<JournalStreak>>()
    }
    val journalStreak by lazy {
        mutableJournalStreak.asSharedFlow()
    }

    /**
     * Setting calendar empty box and increment month if any
     * */
    fun settingCalendarData() {

        getDatesForCalendar(
            incrementMonth = incrementMonth
            ,
            calendarData = { data, year, month ->

                adapter.selectedPosition = -1
                selectedMonth = month
                selectedYear = year

                retryApiRequest(
                    apiName = ApiConstant.JOURNAL_STREAK
                )

                date.set(
                    formatDate(
                        date = "$year-$month",
                        inputFormat = "yyyy-MM",
                        outPutFormat = "MMMM yyyy"
                    )
                )

                val dayOfWeek = data[0].weekOfDay
                val emptyBox = when {
                    (dayOfWeek == "TUE") -> 1
                    (dayOfWeek == "WED") -> 2
                    (dayOfWeek == "THU") -> 3
                    (dayOfWeek == "FRI") -> 4
                    (dayOfWeek == "SAT") -> 5
                    (dayOfWeek == "SUN") -> 6
                    else -> 0
                }

                if (emptyBox != 0) {
                    for (numberOFEmptyBox in 1..emptyBox) {
                        data.add(
                            numberOFEmptyBox - 1, CalendarDate(
                                date = null,
                                year = null,
                                month = null,
                                weekOfDay = daysList[numberOFEmptyBox - 1],
                                isEmpty = true
                            )
                        )
                    }
                }
                adapter.submitList(data)
            }
        )
    }

    fun increment(isIncrement: Boolean) {
        incrementMonth = if (isIncrement) incrementMonth.plus(1) else incrementMonth.minus(1)
        settingCalendarData()
    }

    private fun getJournalStreak() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.MONTH] = selectedMonth.toString()
            hashMap[ApiConstant.YEAR] = selectedYear.toString()

            mutableJournalStreak.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.journalStreak(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.JOURNAL_STREAK
            )
            mutableJournalStreak.emit(value = response)
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.JOURNAL_STREAK -> getJournalStreak()
        }
    }
}