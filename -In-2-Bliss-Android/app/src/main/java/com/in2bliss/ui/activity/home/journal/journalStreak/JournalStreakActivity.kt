package com.in2bliss.ui.activity.home.journal.journalStreak

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.journalStreak.JournalData
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.data.model.journalStreak.JournalStreak
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityJournalStreakBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.addAffirmation.AddAffirmationActivity
import com.in2bliss.ui.activity.home.fragment.favourites.FavouritesTextAffirmationActivity
import com.in2bliss.ui.activity.imagePicker
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.isPastDate
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JournalStreakActivity : BaseActivity<ActivityJournalStreakBinding>(
    layout = R.layout.activity_journal_streak
) {
    var journalStreak: JournalStreak? = null
    private val viewModel: JournalStreakViewModel by viewModels()

    override fun init() {
        binding.data = viewModel
        onClick()
        settingRecyclerView()
        gettingBundleData()
        observer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.settingCalendarData()

    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.journalStreak.collectLatest {
                handleResponse(
                    response = it,
                    context = this@JournalStreakActivity,
                    success = { response ->

                        journalStreak = response
                        viewModel.currentStreak.set("${response.streak.currentStreak ?: 0} Days")
                        viewModel.totalEntries.set("${response.streak.totalEntries ?: 0} Days")
                        viewModel.longestWeek.set("${response.streak.maxStreak ?: 0} Days")

                        if (response.data?.isEmpty() == true) {
                            return@handleResponse
                        }

                        val selectedDates: HashSet<JournalData> = hashSetOf()

                        response.data?.forEach { data ->
                            viewModel.adapter.currentList.forEachIndexed { index, calendarDate ->
                                val calDate = formatDate(
                                    date = "${calendarDate.year}-${calendarDate.month}-${calendarDate.date}",
                                    inputFormat = "yyyy-MM-dd",
                                    outPutFormat = "yyyy-MM-dd"
                                )
                                val backendDate = formatDate(
                                    date = data?.date ?: "",
                                    inputFormat = "yyyy-MM-dd",
                                    outPutFormat = "yyyy-MM-dd"
                                )

                                if (calDate == backendDate) {
                                    val journalDetail = JournalDetail(
                                        description = data?.description.orEmpty(),
                                        backgroundImage = data?.background.orEmpty(),
                                        id = data?.id.toString(),
                                        date = data?.date.orEmpty()
                                    )
                                    selectedDates.add(
                                        JournalData(
                                            index = index,
                                            data = Gson().toJson(journalDetail)
                                        )
                                    )
                                }
                            }
                        }
                        selectedDates.forEach { selectedData ->
                            viewModel.adapter.currentList[selectedData.index].isEvent = true
                            viewModel.adapter.currentList[selectedData.index].data =
                                selectedData.data
                            viewModel.adapter.notifyItemChanged(selectedData.index)
                        }
                    },
                    showToast = false,
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }
    }

    private fun gettingBundleData() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { name ->
            viewModel.categoryName = when (name) {
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                else -> null
            }
        }
    }

    private fun settingRecyclerView() {
        val layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendar.layoutManager = layoutManager
        binding.rvCalendar.itemAnimator = null
        binding.rvCalendar.adapter = viewModel.adapter
        viewModel.adapter.listener = { position, date, data ->

            viewModel.selectedDate = date
            viewModel.journalData = data

            if (data != null) {
                viewModel.position = position

                journalSelectionBottomSheet(
                    journalData = data
                )
            }
        }
    }

    private fun journalSelectionBottomSheet(journalData: String) {
        imagePicker(
            context = this,
            select = { selectedType ->

                /** Edit journal screen */
                if (selectedType == 0) {
                    val bundle = bundleOf(
                        AppConstant.JOURNAL_DATA to journalData,
                        AppConstant.CATEGORY_NAME to viewModel.categoryName?.name,
                        AppConstant.DATE to viewModel.selectedDate,
                        /** sending this data to setup on JournalActivity's toolbar**/
                        AppConstant.STREAK_COUNT to (journalStreak?.streak?.currentStreak
                            ?: 0).toString()
                    )

                    activityResult.launch(
                        Intent(
                            this, AddAffirmationActivity::class.java
                        ).apply {
                            putExtras(bundle)
                        }
                    )
                    return@imagePicker
                }

                /** Navigating to preview journal screen */
                val bundle = bundleOf(
                    AppConstant.JOURNAL_DATA to journalData,
                    AppConstant.CATEGORY_NAME to viewModel.categoryName?.name,
                    AppConstant.CHANGE_GRAVITY to "* just dummy entry",

                    )
                intent(
                    destination = FavouritesTextAffirmationActivity::class.java,
                    bundle = bundle
                )
            },
            title = getString(R.string.journal),
            text1 = getString(R.string.edit_journal),
            text2 = getString(R.string.preview_journal)
        )
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivArrowLeft.setOnClickListener {
            viewModel.increment(
                isIncrement = false
            )
        }

        binding.ivArrowRight.setOnClickListener {
            viewModel.increment(
                isIncrement = true
            )
        }

        binding.btnAddSession.setOnClickListener {

            if (viewModel.selectedDate == null) {
                showToast(
                    message = getString(R.string.please_select_date)
                )
                return@setOnClickListener
            }

            if (viewModel.journalData != null) {
                showToast(
                    message = getString(R.string.journal_is_added_at_selected_date_please_select_different_date)
                )
                return@setOnClickListener
            }

            if (isPastDate(
                    selectedDate = viewModel.selectedDate ?: ""
                )
            ) {
                showToast(
                    message = getString(R.string.please_select_valid_date)
                )
                return@setOnClickListener
            }

            val bundle = bundleOf(
                AppConstant.CATEGORY_NAME to viewModel.categoryName?.name,
                AppConstant.DATE to viewModel.selectedDate,
                /** sending this data to setup on JournalActivity's toolbar**/
                AppConstant.STREAK_COUNT to (journalStreak?.streak?.currentStreak
                    ?: 0).toString()
            )
            addAffirmationResult.launch(
                Intent(
                    this,
                    AddAffirmationActivity::class.java
                ).apply {
                    putExtras(bundle)
                }
            )
        }
    }

    private val addAffirmationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.JOURNAL_STREAK
                )
            }
        }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(AppConstant.JOURNAL_DATA)?.let { data ->
                    viewModel.position?.let { row ->
                        viewModel.adapter.currentList[row].data = data
                        viewModel.adapter.notifyItemChanged(row)
                    }
                }
            }
        }
}