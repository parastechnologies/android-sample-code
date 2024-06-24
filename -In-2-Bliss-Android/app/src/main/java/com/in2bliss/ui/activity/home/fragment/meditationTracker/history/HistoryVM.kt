package com.in2bliss.ui.activity.home.fragment.meditationTracker.history

import androidx.databinding.ObservableField
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.CalendarDate
import com.in2bliss.data.model.MeditationTrackerDateHistoryResponse
import com.in2bliss.data.model.MeditationTrackerHistoryResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.home.journal.journalStreak.CalendarAdapter
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.getDatesForCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
    private val requestManager: RequestManager
) : BaseViewModel() {

    val date = ObservableField("")
    val daysOff = ObservableField("")

    private var incrementMonth: Int = 0
    private var selectedMonth: Int? = null
    private var selectedYear: Int? = null
    var selectedDate: String? = null
    private val daysList = arrayListOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    var position: Int? = null
    var viewPagerPosition = 0
    var categoryId:Int? = 0
    var isFirstTime = true
    var meditationList: List<MeditationTrackerDateHistoryResponse.Data?>? = null

    private val mutableMeditationHistoryResponse by lazy {
        MutableSharedFlow<Resource<MeditationTrackerHistoryResponse>>()
    }
    val mediationTrackerResponse by lazy { mutableMeditationHistoryResponse.asSharedFlow() }

    private val mutableMeditationHistoryDeleteResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val mediationTrackerDeleteResponse by lazy { mutableMeditationHistoryDeleteResponse.asSharedFlow() }

    private val mutableMeditationDateResponse by lazy {
        MutableSharedFlow<Resource<MeditationTrackerDateHistoryResponse>>()
    }
    val mediationTrackerDateResponse by lazy { mutableMeditationDateResponse.asSharedFlow() }

    val historyAdapter by lazy {
        HistoryAdapter(
            requestManager = requestManager
        )
    }

    val adapter by lazy {
        CalendarAdapter()
    }

    /**
     * Setting calendar empty box and increment month if any
     * */
    fun settingCalendarData() {

        getDatesForCalendar(
            incrementMonth = incrementMonth,
            calendarData = { data, year, month ->

                date.set(
                    formatDate(
                        date = "$year-$month",
                        inputFormat = "yyyy-MM",
                        outPutFormat = "MMMM yyyy"
                    )
                )

                selectedMonth = month
                selectedYear = year

                if (viewPagerPosition == 1) {
                    retryApiRequest(
                        apiName = ApiConstant.MEDITATION_SESSION_HISTORY
                    )
                }

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
        incrementMonth = if (isIncrement) {
            incrementMonth = if (incrementMonth < 0) incrementMonth.plus(1) else 0
            incrementMonth
        } else incrementMonth.minus(1)
        settingCalendarData()
    }

    /**
     * meditation/session/history api request
     * */
    private fun mediationSessionHistory() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.MONTH] = selectedMonth.toString()
            hashMap[ApiConstant.YEAR] = selectedYear.toString()

            mutableMeditationHistoryResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.meditationTrackerHistory(hashMap)
                },
                apiName = ApiConstant.MEDITATION_SESSION_HISTORY
            )
            mutableMeditationHistoryResponse.emit(
                value = response
            )
        }
    }

    private fun deleteMeditationTracker(){
        networkCallIo {
            val hashMap=HashMap<String,String>()
            hashMap[AppConstant.SMALL_ID]=categoryId.toString()
            mutableMeditationHistoryResponse.emit(value = Resource.Loading())
            val response= safeApiRequest(
                apiRequest = {
                    apiHelperInterface.meditationTrackerDelete(hashMap)
                },
                apiName = ApiConstant.MEDITATION_SESSION_DELETE
            )
            mutableMeditationHistoryDeleteResponse.emit(
                value = response
            )
        }
    }


    /**
     * meditation/session/history api request
     * */
    private fun mediationSessionDate() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.DATE] = selectedDate ?: getCurrentDate()

            mutableMeditationDateResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.meditationTrackerDate(hashMap)
                },
                apiName = ApiConstant.MEDITATION_SESSION_DATE
            )
            mutableMeditationDateResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.MEDITATION_SESSION_HISTORY -> mediationSessionHistory()
            ApiConstant.MEDITATION_SESSION_DATE -> mediationSessionDate()
            ApiConstant.MEDITATION_SESSION_DELETE -> deleteMeditationTracker()
        }
    }
}