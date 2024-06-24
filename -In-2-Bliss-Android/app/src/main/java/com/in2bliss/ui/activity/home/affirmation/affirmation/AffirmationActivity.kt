package com.in2bliss.ui.activity.home.affirmation.affirmation

import androidx.activity.viewModels
import androidx.core.os.bundleOf
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityAffirmationBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffirmationActivity : BaseActivity<ActivityAffirmationBinding>(
    layout = R.layout.activity_affirmation
) {

    private val viewModel: AffirmationViewModel by viewModels()

    override fun init() {
        binding.data = viewModel
        gettingBundle()
        onClick()
    }

    private fun gettingBundle() {
        intent.getBooleanExtra(AppConstant.IS_SEARCH, false).let { isSearch ->
            viewModel.isSearch = isSearch
        }
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryName = when (categoryName) {
                AppConstant.HomeCategory.QUOTES.name -> AppConstant.HomeCategory.QUOTES
                else -> null
            }
        }
        binding.affirmation.visibility(viewModel.isSearch.not())
        binding.search.visibility(viewModel.isSearch)
        val title = when (viewModel.categoryName) {
            AppConstant.HomeCategory.QUOTES -> "I trust that everything will work out in the end, and I release my anxiety and worry."
            else -> ""
        }
        binding.tvAffirmation.text = title
        binding.addAndFav.visibility(viewModel.categoryName != AppConstant.HomeCategory.QUOTES)
        binding.quotes.visibility(viewModel.categoryName == AppConstant.HomeCategory.QUOTES)
        if (viewModel.categoryName == AppConstant.HomeCategory.QUOTES) {
            binding.affirmation.gone()
            binding.search.gone()
        }
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAffirmationShrink.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddAffirmationAdd.setOnClickListener {
            intent(
                destination = AffirmationListActivity::class.java
            )
        }

        binding.ivMenu.setOnClickListener {
            intent(
                destination = AffirmationCategoriesActivity::class.java,
                bundle = bundleOf(AppConstant.IS_AFFIRMATION_CATEGORIES_FILTER to true)
            )
        }
    }
}

