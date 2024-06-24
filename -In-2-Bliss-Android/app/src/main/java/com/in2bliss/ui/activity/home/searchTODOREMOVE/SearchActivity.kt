package com.in2bliss.ui.activity.home.searchTODOREMOVE

import androidx.activity.viewModels
import androidx.core.os.bundleOf
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivitySearchBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListActivity
import com.in2bliss.ui.activity.home.searchTODOREMOVE.searchFilter.SearchFilterBottomSheet
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>(
    layout = R.layout.activity_search
) {

    private val viewModel: SearchViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.search)
        binding.data = viewModel
        gettingBundle()
        settingRecyclerView()
        onclick()
    }

    private fun gettingBundle() {
        intent.getBooleanExtra(AppConstant.IS_SEARCH_FILTER_RESULT, false)
            .let { isSearchFilterResult ->
                viewModel.isSearchFilterResult = isSearchFilterResult
            }

        binding.search.visibility(
            isVisible = viewModel.isSearchFilterResult.not()
        )
        binding.searchFilter.visibility(
            isVisible = viewModel.isSearchFilterResult
        )

        if (viewModel.isSearchFilterResult) viewModel.showResult.set("Showing 145 results")
    }

    private fun settingRecyclerView() {
        /** Recent search */
        binding.rvRecentSearch.adapter = viewModel.recentSearchedAdapter
        binding.rvRecentSearch.itemAnimator = null
        if (viewModel.isSearchFilterResult.not()) viewModel.recentSearchedAdapter.submitList(
            viewModel.recentChatList
        )
        viewModel.recentSearchedAdapter.deleteListener = { position ->
            val currentList = viewModel.recentSearchedAdapter.currentList.toMutableList()
            currentList.removeAt(position)
            viewModel.recentSearchedAdapter.submitList(currentList)
        }

        /** Search keys */
        binding.rvSearchKeys.adapter = viewModel.searchKeywordAdapter
        binding.rvSearchKeys.itemAnimator = null
        viewModel.searchKeywordAdapter.submitList(viewModel.searchKeyword)

        /** Search filter result */
        binding.rvSearchFilter.adapter = viewModel.searchFilerResultAdapter
        binding.rvSearchFilter.itemAnimator = null
        if (viewModel.isSearchFilterResult) viewModel.searchFilerResultAdapter.submitList(viewModel.searchFilterList)
    }

    private fun onclick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.ivFilter.setOnClickListener {
            SearchFilterBottomSheet().show(supportFragmentManager, null)
        }

        binding.ivArrow.setOnClickListener {
            intent(
                destination = AffirmationListActivity::class.java,
                bundle = bundleOf(AppConstant.IS_SEARCH to true)
            )
        }
    }
}