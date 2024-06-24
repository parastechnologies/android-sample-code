package com.in2bliss.ui.activity.home.fragment.meditationTracker.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.in2bliss.databinding.FragmentSelectMeditationTypeBottomsheetBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesActivity
import com.in2bliss.ui.activity.home.affirmationDetails.customizeAffirmationBottomSheet.CustomiseMediationTrackerFragment
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast


class SelectMeditationTypeBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentSelectMeditationTypeBottomsheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectMeditationTypeBottomsheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 3000
        setAdapter()
    }

    private fun setAdapter() {
        val list =
            arrayListOf("Guided Meditation", "Silent Meditation", "Meditate with Music")
        val adapter = SelectMeditationTypeAdapter()
        binding.rvRecyclerView.adapter = adapter
        adapter.submitList(list)
        adapter.callBack = { pos ->
            when (pos) {
                0 -> {
                    val bundle =
                        bundleOf(
                            AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.GUIDED_MEDITATION.name,
                            AppConstant.TYPE to AppConstant.TYPE
                        )
                    requireActivity().intent(
                        destination = AffirmationCategoriesActivity::class.java,
                        bundle = bundle
                    )
                    dismiss()
                }

                1 -> {
                    CustomiseMediationTrackerFragment().apply {
                        arguments = bundleOf(
                            AppConstant.MEDITATION_TYPE to AppConstant.SILENT_MEDITATION
                        )
                    }.also {
                        it.show(
                            childFragmentManager, null
                        )
                    }

                }

                else -> {
                    CustomiseMediationTrackerFragment().apply {
                        arguments = bundleOf(
                                AppConstant.MEDITATION_TYPE to AppConstant.MUSIC_MEDITATION
                        )
                    }.also {
                        it.show(
                            childFragmentManager, null
                        )
                    }
                }
            }
        }
    }
}