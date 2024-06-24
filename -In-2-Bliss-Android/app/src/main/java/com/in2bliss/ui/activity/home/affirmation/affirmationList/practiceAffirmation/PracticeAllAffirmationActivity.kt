package com.in2bliss.ui.activity.home.affirmation.affirmationList.practiceAffirmation

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.databinding.ActivityPracticeAllAffirmationBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PracticeAllAffirmationActivity : BaseActivity<ActivityPracticeAllAffirmationBinding>(
    layout = R.layout.activity_practice_all_affirmation
) {

    @Inject
    lateinit var requestManager: RequestManager

    private val viewModel: AffirmationListViewModel by viewModels()


    override fun init() {
        binding.data = viewModel
        getIntentData()
        observer()
        onClick()
        settingRecyclerView()
    }

    /**
     * get intent data [getIntentData]
     * */
    private fun getIntentData() {

        /** Getting the category */
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryType = when (categoryName) {
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                AppConstant.HomeCategory.TEXT_AFFIRMATION.name -> AppConstant.HomeCategory.TEXT_AFFIRMATION
                else -> null
            }
        }
        Log.d("ascsvhacsha", "getIntentData: ${viewModel.categoryType}")

        when (viewModel.categoryType) {
            AppConstant.HomeCategory.JOURNAL -> {
                intent.getStringExtra(AppConstant.JOURNAL_DATA)?.let { journalData ->
                    val data = Gson().fromJson(
                        journalData,
                        JournalDetail::class.java
                    )
                    setData(data)
                }
            }

            AppConstant.HomeCategory.TEXT_AFFIRMATION -> {

                intent.getStringExtra(AppConstant.JOURNAL_DATA)?.let { journalData ->
                    val data = Gson().fromJson(
                        journalData,
                        JournalDetail::class.java
                    )
                    setData(
                        data, isTextAffirmation = true
                    )
                }
            }

            else -> {}
        }
        binding.clAffirmationDetail.visibility(isVisible = viewModel.categoryType == AppConstant.HomeCategory.JOURNAL)
        binding.clAffirmationDetail.visibility(isVisible = viewModel.categoryType == AppConstant.HomeCategory.TEXT_AFFIRMATION)
        binding.scroller.visibility(isVisible = viewModel.categoryType != AppConstant.HomeCategory.JOURNAL)

        if (intent.hasExtra(AppConstant.AFFIRMATION)) {
            viewModel.affirmationId = intent.extras?.getString(AppConstant.AFFIRMATION)
        }
    }

    private fun setData(
        data: JournalDetail?, isTextAffirmation: Boolean = false
    ) {
        binding.tvAffirmation.text = data?.description

        binding.tvCategory.text = if (isTextAffirmation) {
            data?.categoryName
        } else {
            formatDate(
                date = data?.date.orEmpty(),
                inputFormat = "yyyy-MM-dd",
                outPutFormat = "MMMM dd, yyyy"
            )
        }

        if (isTextAffirmation) {
            data?.categoryIcon.let { image ->
                binding.ivCategory.glide(
                    requestManager = requestManager,
                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
                    error = R.color.prime_purple_5F46F4,
                    placeholder = R.color.prime_purple_5F46F4
                )
            }
        }

        val backgroundImage = when (viewModel.categoryType) {
            AppConstant.HomeCategory.JOURNAL -> {
                BuildConfig.JOURNAL_BASE_URL.plus(data?.backgroundImage ?: "")
            }

            AppConstant.HomeCategory.TEXT_AFFIRMATION -> {
                BuildConfig.AFFIRMATION_BASE_URL.plus(data?.backgroundImage ?: "")
            }

            else -> data?.backgroundImage ?: ""
        }

        binding.ivAffirmationBg.glide(
            requestManager = requestManager,
            image = backgroundImage,
            error = R.color.black,
            placeholder = R.color.black
        )
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivBack2.setOnClickListener {
            finish()
        }
    }

    private fun observer() {
        when (viewModel.categoryType) {
            AppConstant.HomeCategory.JOURNAL -> {

            }

            else -> {
                viewModel.viewPagerAdapter.addLoadStateListener {
                    when (it.refresh) {
                        is LoadState.Error -> {
                            binding.pbProgress.gone()
                        }

                        is LoadState.Loading -> {
                            binding.pbProgress.visible()
                        }

                        is LoadState.NotLoading -> {
                            binding.pbProgress.gone()
                        }
                    }
                }
                lifecycleScope.launch {
                    viewModel.getTextAffirmationList().collectLatest { textAffirmationList ->
                        lifecycleScope.launch {
                            viewModel.viewPagerAdapter.submitData(textAffirmationList)
                        }
                    }
                }
            }
        }
    }

    private fun settingRecyclerView() {
        binding.rvRecyclerView.adapter = viewModel.viewPagerAdapter
    }
}