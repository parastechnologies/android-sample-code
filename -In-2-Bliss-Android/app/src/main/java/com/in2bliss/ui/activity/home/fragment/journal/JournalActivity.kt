package com.in2bliss.ui.activity.home.fragment.journal

import android.app.Activity
import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityJournalBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.editAffirmationBottomSheet
import com.in2bliss.ui.activity.home.affirmation.addAffirmation.AddAffirmationActivity
import com.in2bliss.ui.activity.home.fragment.favourites.FavouritesTextAffirmationActivity
import com.in2bliss.ui.activity.home.journal.journalStreak.JournalStreakActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JournalActivity : BaseActivity<ActivityJournalBinding>(
    layout = R.layout.activity_journal
) {
    private val viewModel: JournalViewModel by viewModels()
    private var job: Job? = null

    override fun init() {
        binding.data = viewModel
        getBundle()
        backPressed()
        settingRecyclerView()
        onClick()
        observer()
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }

    private fun getBundle() {

        /** if streak is coming from previous screen then setting up to toolbar**/
        AppConstant.STREAK_COUNT.let { key ->
            if (intent.hasExtra(key)) {
                viewModel.streak.set(intent.getStringExtra(key))
                if (viewModel.streak.get()=="0"){
                    binding.cvFireContainer.gone()
                }else{
                    binding.cvFireContainer.visible()

                }
            }
        }
        /** Getting the category */
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryType = when (categoryName) {
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                else -> null
            }
        }
    }

    private fun onClick() {
        binding.ivAddJournal.setOnClickListener {
            intent(
                destination = JournalStreakActivity::class.java
            )
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.edtSearch.doAfterTextChanged { text ->
            binding.ivCancelSearch.visibility((text?.length ?: "".length) > 0)
        }

        binding.ivCancelSearch.setOnClickListener {
            viewModel.search.set("")
            viewModel.retryApiRequest(
                apiName = ApiConstant.GUIDED_MEDIATION
            )
            journalListing()
        }

        binding.edtSearch.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) journalListing()
            p1 == EditorInfo.IME_ACTION_SEARCH
        }
    }

    fun observer() {

        viewModel.adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbJournal.gone()
                    binding.pbNewJournalData.gone()
                }

                is LoadState.Loading -> {

                    val isEmptyList = viewModel.adapter.snapshot().isEmpty()
                    binding.pbJournal.visibility(isVisible = isEmptyList)
                    binding.pbNewJournalData.visibility(isVisible = isEmptyList.not())
                }

                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.adapter.snapshot().items.isEmpty()
                    binding.tvNoDatFound.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbJournal.gone()
                    binding.pbNewJournalData.gone()
                }
            }
        }

        journalListing()

        lifecycleScope.launch {
            viewModel.deleteJournal.collectLatest {
                handleResponse(
                    response = it,
                    context = this@JournalActivity,
                    success = { _ ->
                        lifecycleScope.launch {
                            viewModel.position?.let { row ->
                                val currentList = viewModel.adapter.snapshot().items.toMutableList()
                                currentList.removeAt(row)
                                viewModel.adapter.submitData(PagingData.from(currentList))
                            }
                        }
                    },
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

    private fun settingRecyclerView() {
        binding.rvJournal.adapter = viewModel.adapter
        (binding.rvJournal.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false

        viewModel.adapter.openDetailScreenCallBack = { data ->
            val bundle = bundleOf(
                AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.JOURNAL.name,
                AppConstant.CHANGE_GRAVITY to "* just dummy entry",
                AppConstant.JOURNAL_DATA to Gson().toJson(
                    JournalDetail(
                        date = data.date ?: "",
                        description = data.description ?: "",
                        backgroundImage = data.background ?: "",
                        id = data.id.toString()
                    )
                )
            )
            intent(
                destination = FavouritesTextAffirmationActivity::class.java,
                bundle = bundle
            )
        }

        viewModel.adapter.editListener = { position, data ->
            viewModel.deleteJournalId = data?.id
            viewModel.position = position

            editAffirmationBottomSheet(
                context = this,
                isFav = false,
                isShare = false,
                fav = {

                },
                edit = {
                    val journalData = Gson().toJson(
                        JournalDetail(
                            date = data?.date ?: "",
                            description = data?.description ?: "",
                            backgroundImage = data?.background ?: "",
                            id = data?.id.toString()
                        )
                    )

                    val bundle = bundleOf(
                        AppConstant.JOURNAL_DATA to journalData,
                        AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
                        AppConstant.DATE to data?.date
                    )

                    activityResult.launch(
                        Intent(
                            this, AddAffirmationActivity::class.java
                        ).apply {
                            putExtras(bundle)
                        }
                    )
                }, share = {

                },
                delete = {
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.DELETE_JOURNAL
                    )
                }
            )
        }
    }

    private fun journalListing() {
        job?.cancel()

        job = lifecycleScope.launch {
            viewModel.getGuidedJournal().observe(
                this@JournalActivity
            ) { guidedJournalList ->
                lifecycleScope.launch {
                    viewModel.adapter.submitData(guidedJournalList)
                }
            }
        }
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(AppConstant.JOURNAL_DATA)?.let { data ->
                    viewModel.position?.let { row ->

                        val journalDetail = Gson().fromJson(data, JournalDetail::class.java)
                        viewModel.adapter.snapshot().items[row].apply {
                            description = journalDetail.description
                        }
                        viewModel.adapter.notifyItemChanged(row)
                    }
                }
            }
        }
}