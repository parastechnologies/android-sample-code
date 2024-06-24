package com.in2bliss.ui.activity.home.affirmationExplore.durationSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.in2bliss.R
import com.in2bliss.data.model.musicList.duration.Duration
import com.in2bliss.databinding.FragmentDurationPickerBinding
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DurationSearchBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDurationPickerBinding
    var selectedDuration: ((start: String, end: String) -> Unit)? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private val adapter by lazy {
        DurationAdapter()
    }

    private val durationList = arrayListOf(
        Duration(
            time = "0-10 minutes",
            startTimeInSeconds = "0",
            endTimeInSeconds = "600"
        ),
        Duration(
            time = "10-20 minutes",
            startTimeInSeconds = "600",
            endTimeInSeconds = "1200"
        ),
        Duration(
            time = "20-30 minutes",
            startTimeInSeconds = "1200",
            endTimeInSeconds = "1800"
        ),
        Duration(
            time = "30-40 minutes",
            startTimeInSeconds = "1800",
            endTimeInSeconds = "2400"
        ),
        Duration(
            time = "40 or more minutes",
            startTimeInSeconds = "2400",
            endTimeInSeconds = "-1"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDurationPickerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingRecyclerView()
        onClick()
    }

    private fun settingRecyclerView() {
        val layoutManger = FlexboxLayoutManager(context)
        layoutManger.flexDirection = FlexDirection.ROW
        binding.rvDuration.layoutManager = layoutManger
        binding.rvDuration.adapter = adapter
        adapter.submitList(durationList)
        adapter.selectedDuration = { start, end ->
            startTime = start
            endTime = end
        }
    }

    private fun onClick() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (startTime == null || endTime == null){
                requireActivity().showToast(
                    message = getString(R.string.please_select_duration)
                )
                return@setOnClickListener
            }
            selectedDuration?.invoke(
                startTime.orEmpty(), endTime.orEmpty()
            )
            dismiss()
        }
    }
}