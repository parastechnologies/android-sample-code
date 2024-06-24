package com.in2bliss.ui.activity.home.fragment.meditationTracker.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.model.MeditationTrackerDateHistoryResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.FragmentHistoryBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.deleteHistory
import com.in2bliss.ui.activity.home.meditationTrackerMeditate.session.StartSessionActivity
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(
    layoutInflater = FragmentHistoryBinding::inflate
) {
    private val viewModel: HistoryVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel

        viewModel.retryApiRequest(
            apiName = ApiConstant.MEDITATION_SESSION_HISTORY
        )
        settingRecyclerView()
        viewModel.settingCalendarData()
        onClick()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.mediationTrackerResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = { response ->

                        if (viewModel.isFirstTime) {
                            viewModel.isFirstTime = false
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.MEDITATION_SESSION_DATE
                            )
                        }
                        val selectedDates: HashSet<Int> = hashSetOf()
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
                                    selectedDates.add(index)
                                }
                            }
                        }

                        selectedDates.forEach { selectedIndex ->
                            viewModel.adapter.currentList[selectedIndex].isEvent = true
                            viewModel.adapter.notifyItemChanged(selectedIndex)
                        }

                    },
                    errorBlock = {
                        if (viewModel.isFirstTime) {
                            viewModel.isFirstTime = false
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.MEDITATION_SESSION_DATE
                            )
                        }
                    },
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
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
        lifecycleScope.launch {
            viewModel.mediationTrackerDateResponse.collectLatest {
                handleResponse(response = it,
                    context = requireActivity(),
                    success = { response ->
                        viewModel.meditationList = response.data
                        viewModel.position?.let { position ->
                            viewModel.adapter.currentList[position].data = Gson().toJson(response)
                            viewModel.adapter.notifyItemChanged(position)
                        }
                        addDataToAdapter()
                    }, error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    },
                    errorBlock = {
                        viewModel.meditationList = null
                        addDataToAdapter()
                    })
            }
        }
        lifecycleScope.launch {
            viewModel.mediationTrackerDeleteResponse.collectLatest {
                handleResponse(response = it,
                    context = requireActivity(),
                    success = { _ ->
                        viewModel.retryApiRequest(ApiConstant.MEDITATION_SESSION_DATE)
                    }, error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    },
                    errorBlock = {
                        viewModel.meditationList = null
                        addDataToAdapter()
                    })
            }
        }
    }

    private fun addDataToAdapter() {
        viewModel.historyAdapter.submitList(viewModel.meditationList) {
            binding.tvNoDataFound.visibility(
                isVisible = viewModel.historyAdapter.currentList.isEmpty()
            )
        }
        viewModel.historyAdapter.deleteListener = {
            deleteHistory(
                activity = requireActivity(),
                delete = {
                    viewModel.categoryId = it
                    viewModel.retryApiRequest(ApiConstant.MEDITATION_SESSION_DELETE)
                }
            )
        }

    }

    private fun onClick() {
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
            requireActivity().intent(
                destination = StartSessionActivity::class.java
            )
        }
    }

    private fun settingRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.layoutManager = layoutManager
        binding.rvCalendar.itemAnimator = null
        binding.rvCalendar.adapter = viewModel.adapter

        viewModel.adapter.listener = { position, selectedDate, data ->
            viewModel.position = position
            viewModel.selectedDate = selectedDate

            if (data == null || selectedDate == getCurrentDate()) {
                viewModel.retryApiRequest(
                    ApiConstant.MEDITATION_SESSION_DATE
                )
            } else {
                val meditationList = try {
                    Gson().fromJson(
                        data,
                        MeditationTrackerDateHistoryResponse::class.java
                    ).data
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    null
                }
                viewModel.meditationList = meditationList
                addDataToAdapter()
            }
        }

        binding.rvHistory.adapter = viewModel.historyAdapter
        (binding.rvHistory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }
}

