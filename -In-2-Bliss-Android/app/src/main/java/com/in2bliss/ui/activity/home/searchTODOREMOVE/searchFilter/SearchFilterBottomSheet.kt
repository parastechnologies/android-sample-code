package com.in2bliss.ui.activity.home.searchTODOREMOVE.searchFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.in2bliss.databinding.FragmentSearchFilterBottomSheetBinding
import com.in2bliss.ui.activity.home.searchTODOREMOVE.SearchActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent

class SearchFilterBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentSearchFilterBottomSheetBinding
    private val viewModel: SearchFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchFilterBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 2400
        binding.data = viewModel
        settingRecyclerView()
        onClick()
    }

    private fun settingRecyclerView() {
        binding.rvSubCategory.adapter = viewModel.adapter
        binding.rvSubCategory.itemAnimator = null
        viewModel.adapter.submitList(viewModel.searchFilter)

        viewModel.adapter.selectedCategoryPositionListener = { position ->
            viewModel.selectedCategoryPosition = position
        }
    }

    private fun onClick() {
        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.tvClear.setOnClickListener {
            viewModel.adapter.currentList[viewModel.selectedCategoryPosition].dataList.forEachIndexed { index, data ->
                if (data.isSelected) {
                    viewModel.adapter.currentList[viewModel.selectedCategoryPosition].dataList[index].isSelected =
                        false
                }
            }
            viewModel.adapter.notifyItemChanged(viewModel.selectedCategoryPosition)
        }

        binding.btnShowResult.setOnClickListener {
            requireActivity().intent(
                destination = SearchActivity::class.java,
                bundle = bundleOf(AppConstant.IS_SEARCH_FILTER_RESULT to true)
            )
        }
    }
}