package com.in2bliss.ui.activity.home.reminder

import android.content.Context
import androidx.databinding.ObservableField
import com.in2bliss.R
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.DaysList
import com.in2bliss.data.model.GetNotificationStatusResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.convertStringToList
import com.in2bliss.utils.extension.getNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val howManyTimes = ObservableField("--/--")
    val startAt = ObservableField("00:00 --")
    val endAt = ObservableField("00:00 --")
    val time = ObservableField("00:00 --")
    var categoryName: AppConstant.HomeCategory? = null
    var isEdit = false
    var notificationAdd = false
    var gratitudeReminderDays: String? = null
    var journalReminderDays: String? = null
    var numberOfTimes: Int? = null
    var type = ""
    var isNoReminder = false
    var bundle = GetNotificationStatusResponse.Quote.Reminder()
    var bundleGratitude = GetNotificationStatusResponse.Journal.Gratitude()

    val gratitudeReminderAdapter by lazy {
        ReminderAdapter()
    }

    val journalReminderAdapter by lazy {
        ReminderAdapter()
    }

    private val mutableSetReminderResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val setReminderResponse by lazy { mutableSetReminderResponse.asSharedFlow() }

    private val mutableUpDateReminderResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val updateReminderResponse by lazy { mutableUpDateReminderResponse.asSharedFlow() }

    /**
     * Journal reminder api request
     * */
    private fun journalReminder() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            if (isNoReminder) {
                hashMap[ApiConstant.STATUS] = "1"
            } else {
                hashMap[ApiConstant.START_TIME] = startAt.get().orEmpty()
                hashMap[ApiConstant.END_TIME] = endAt.get().orEmpty()
                hashMap[ApiConstant.INTERVAL] = getNumber(howManyTimes.get().toString()).toString()
                hashMap[ApiConstant.GRATITUDE_DAYS] = gratitudeReminderDays.orEmpty()
                hashMap[ApiConstant.JOURNAL_DAYS] = journalReminderDays.orEmpty()
                hashMap[ApiConstant.TIME] = time.get().orEmpty()
                hashMap[ApiConstant.STATUS] = "0"
            }

            mutableSetReminderResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.setJournalReminder(hashMap)
                },
                apiName = ApiConstant.REMINDER_SET
            )
            mutableSetReminderResponse.emit(
                value = response
            )
        }
    }

    /**
     * update reminder api request
     * */
    private fun updateReminder() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.ID] = bundle.id.toString()
            hashMap[ApiConstant.START_TIME] = startAt.get().orEmpty()
            hashMap[ApiConstant.END_TIME] = endAt.get().orEmpty()
            hashMap[ApiConstant.INTERVAL] = getNumber(howManyTimes.get().toString()).toString()
            hashMap[ApiConstant.TYPE] = bundle.type.toString()
            hashMap[ApiConstant.DAYS] = gratitudeReminderDays.orEmpty()

            mutableSetReminderResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.updateReminder(hashMap)
                },
                apiName = ApiConstant.REMINDER_UPDATE
            )
            mutableSetReminderResponse.emit(
                value = response
            )
        }
    }


    /**
     * Reminder api request
     * */
    private fun setReminder() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.START_TIME] = startAt.get().orEmpty()
            hashMap[ApiConstant.END_TIME] = endAt.get().orEmpty()
            hashMap[ApiConstant.INTERVAL] = getNumber(howManyTimes.get().toString()).toString()
            hashMap[ApiConstant.DAYS] = gratitudeReminderDays.orEmpty()
            val type = when (categoryName) {
                AppConstant.HomeCategory.JOURNAL -> 1
                AppConstant.HomeCategory.QUOTES -> 3
                else -> 0
            }
            hashMap[ApiConstant.TYPE] = type.toString()

            mutableSetReminderResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.setReminder(hashMap)
                },
                apiName = ApiConstant.REMINDER_SET
            )
            mutableSetReminderResponse.emit(
                value = response
            )
        }
    }

    fun getReminderDaysList(): ArrayList<DaysList> {
        return arrayListOf(
            DaysList(
                "Su",
                false,
                "Sunday"
            ),
            DaysList(
                "M",
                false,
                "Monday"
            ),
            DaysList(
                "Tu",
                false,
                "Tuesday"
            ),
            DaysList(
                "We",
                false,
                "Wednesday"
            ),
            DaysList(
                "Th", false, "Thursday"
            ),
            DaysList(
                "F",
                false,
                "Friday"
            ),
            DaysList(
                "Sa",
                false,
                "Saturday"
            )
        )
    }


    fun setReminderData(
        context: Context
    ) {
        when (categoryName) {
            AppConstant.HomeCategory.JOURNAL -> {

                val tempListGratitude = bundleGratitude.days?.let { convertStringToList(it) }
                startAt.set(bundleGratitude.startTime)
                endAt.set(bundleGratitude.endTime)
                numberOfTimes = bundleGratitude.interval
                howManyTimes.set("$numberOfTimes ${context.getString(R.string.times_day)}")
                tempListGratitude?.forEach {
                    gratitudeReminderAdapter.currentList.forEachIndexed { index, daysList ->
                        if (daysList.fullName == it) {
                            val currentList = gratitudeReminderAdapter.currentList.toMutableList()
                            currentList[index].isSelected = true
                            gratitudeReminderAdapter.notifyItemChanged(index)
                        }
                    }
                }

                val tempList = bundle.days?.let { convertStringToList(it) }
                time.set(bundle.startTime)
                tempList?.forEach {
                    journalReminderAdapter.currentList.forEachIndexed { index, daysList ->
                        if (daysList.fullName == it) {
                            val currentList = journalReminderAdapter.currentList.toMutableList()
                            currentList[index].isSelected = true
                            journalReminderAdapter.notifyItemChanged(index)
                        }
                    }
                }
            }

            else -> {
                val tempList = bundle.days?.let { convertStringToList(it) }
                startAt.set(bundle.startTime)
                endAt.set(bundle.endTime)
                numberOfTimes = bundle.interval
                howManyTimes.set("$numberOfTimes ${context.getString(R.string.times_day)}")
                tempList?.forEach {
                    gratitudeReminderAdapter.currentList.forEachIndexed { index, daysList ->
                        if (daysList.fullName == it) {
                            val currentList = gratitudeReminderAdapter.currentList.toMutableList()
                            currentList[index].isSelected = true
                            gratitudeReminderAdapter.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.REMINDER_SET -> setReminder()
            ApiConstant.JOURNAL_REMINDER -> journalReminder()
            ApiConstant.REMINDER_UPDATE -> updateReminder()
        }
    }
}