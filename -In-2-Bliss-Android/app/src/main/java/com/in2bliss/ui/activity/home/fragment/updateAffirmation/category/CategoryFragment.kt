package com.in2bliss.ui.activity.home.fragment.updateAffirmation.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.in2bliss.base.BaseFragment
import com.in2bliss.databinding.FragmentCategoryBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(
    layoutInflater = FragmentCategoryBinding::inflate
) {
    private val viewModel: AffirmationCategoriesViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingRecyclerView()
        onClick()
    }
    private fun settingRecyclerView() {
        binding.rvCategory.adapter = viewModel.adapter
        binding.rvCategory.itemAnimator = null
//        viewModel.adapter.submitList(viewModel.mainCategoryList)

        viewModel.adapter.addListener = { position,_ ->

//            if (subCateList.isNotEmpty()) {
//                viewModel.openingSubCategoriesBottomSheet(
//                    subCateList = subCateList,
//                    subCatTitle = subCatTitle,
//                    position = position,
//                    supportFragmentManager = requireActivity().supportFragmentManager
//                )
//            }
        }
    }

    private fun onClick() {

    }

}

