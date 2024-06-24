package com.in2bliss.ui.activity.home.affirmation.affirmationList

import android.app.Activity
import android.content.Intent
import android.view.inputmethod.EditorInfo
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
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityAffirmationListBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.editAffirmationBottomSheet
import com.in2bliss.ui.activity.home.affirmation.addAffirmation.AddAffirmationActivity
import com.in2bliss.ui.activity.home.affirmation.affirmation.AffirmationActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.practiceAffirmation.PracticeAllAffirmationActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.hideKeyboard
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AffirmationListActivity : BaseActivity<ActivityAffirmationListBinding>(
    layout = R.layout.activity_affirmation_list
) {

    private val viewModel: AffirmationListViewModel by viewModels()

    private var job: Job? = null

    var isHome = true

    override fun init() {
        binding.data = viewModel
        gettingBundle()
        onClick()
        settingRecyclerView()
        observer()
    }

    private fun observer() {
        viewModel.adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbProgress.gone()
                    binding.pbNewData.gone()
                }

                is LoadState.Loading -> {
                    val isListEmpty = viewModel.adapter.snapshot().items.isEmpty()
                    binding.pbProgress.visibility(isVisible = isListEmpty)
                    binding.pbNewData.visibility(isVisible = isListEmpty.not())
                }

                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.adapter.snapshot().items.isEmpty()
                    binding.tvNoAffirmationAdded.visibility(
                        isVisible = isListEmpty
                    )
                    binding.btnPracticeAllAffirmation.visibility(
                        isVisible = isListEmpty.not()
                    )
                    binding.pbProgress.gone()
                    binding.pbNewData.gone()
                }
            }
        }

        getAddedTextAffirmation()

        /** Delete affirmation */
        lifecycleScope.launch {
            viewModel.affirmationDelete.collectLatest {
                handleResponse(response = it, context = this@AffirmationListActivity, success = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val currentList = viewModel.adapter.snapshot().items.toMutableList()
                        currentList.removeAt(viewModel.position)
                        viewModel.adapter.submitData(pagingData = PagingData.from(currentList))
                        getAddedTextAffirmation()
                        binding.tvNoAffirmationAdded.visibility(isVisible = currentList.isEmpty())
                    }
                }, error = { message, apiName ->
                    alertDialogBox(
                        message = message
                    ) {
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    }
                })
            }
        }

        /** Favourite affirmation */
        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(response = it,
                    context = this@AffirmationListActivity,
                    success = { _ ->
                        lifecycleScope.launch {
                            val currentList = viewModel.adapter.snapshot().items.toMutableList()
                            currentList[viewModel.position].favouriteStatus =
                                if (viewModel.isFavourite) 0 else 1
                            viewModel.adapter.submitData(PagingData.from(currentList.toMutableList()))
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
                    })
            }
        }
    }

    private fun getAddedTextAffirmation() {
        if (job != null) job?.cancel()
        job = lifecycleScope.launch {
            ensureActive()
            viewModel.getTextAffirmationList().collectLatest { textAffirmationList ->
                lifecycleScope.launch {
                    viewModel.adapter.submitData(textAffirmationList)
                    if (viewModel.adapter.snapshot().items.isEmpty()) binding.btnPracticeAllAffirmation.gone() else binding.btnPracticeAllAffirmation.visible()
                }
            }
        }
    }

    private fun gettingBundle() {
        intent.getBooleanExtra(AppConstant.IS_SEARCH, false).let { isSearch ->
            viewModel.isSearch = isSearch
        }
        binding.toolBar.tvTitle.setText(if (viewModel.isSearch) R.string.search else R.string.my_affirmations)
        binding.affirmation.visibility(viewModel.isSearch.not())
        binding.search.visibility(viewModel.isSearch)
    }

    private fun settingRecyclerView() {
        binding.rvAffirmationList.adapter = viewModel.adapter
        (binding.rvAffirmationList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        viewModel.adapter.listener = { position, affirmationId, data ->
            viewModel.position = position
            editAffirmationBottomSheet(
                context = this,
                isDelete = viewModel.isSearch.not(),
                isEdit = viewModel.isSearch.not(),
                favStatus = data?.favouriteStatus == 1,
                fav = {
                    viewModel.affirmationID = affirmationId
                    viewModel.isFavourite = data?.favouriteStatus == 1
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.FAVOURITE_AFFIRMATION
                    )
                },
                edit = {
                    val bundle = bundleOf(
                        AppConstant.TEXT_AFFIRMATION_DATA to Gson().toJson(data),
                        AppConstant.EDIT to true
                    )
                    activityResult.launch(Intent(this, AddAffirmationActivity::class.java).apply {
                        putExtras(bundle)
                    })
                },
                share = {

                },
                delete = {
                    viewModel.affirmationID = affirmationId
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.DELETE_AFFIRMATION
                    )
                },
                isShare = false
            )
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.addAffirmation.setOnClickListener {
            activityResult.launch(
                Intent(this, AddAffirmationActivity::class.java)
            )
        }

        binding.btnPracticeAllAffirmation.setOnClickListener {
            if (viewModel.isSearch) {
                intent(
                    destination = AffirmationActivity::class.java,
                    bundle = bundleOf(AppConstant.IS_SEARCH to true)
                )
                finish()
                return@setOnClickListener
            }
            intent(
                destination = PracticeAllAffirmationActivity::class.java,
            )
        }

        binding.edtSearch.doAfterTextChanged { text ->
            binding.ivCancelSearch.visibility((text?.length ?: "".length) > 0)
        }

        binding.ivCancelSearch.setOnClickListener {
            viewModel.search.set("")
            getAddedTextAffirmation()
        }

        binding.edtSearch.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                getAddedTextAffirmation()
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_SEARCH
        }

    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                getAddedTextAffirmation()
            }
        }
}