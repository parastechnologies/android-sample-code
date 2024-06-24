package com.in2bliss.ui.activity.home.myAffirmation

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityMyAffirmationBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.editAffirmationBottomSheet
import com.in2bliss.ui.activity.home.affirmation.addAffirmation.AddAffirmationActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListViewModel
import com.in2bliss.ui.activity.home.seeAll.SeeAllViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
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
class MyAffirmationActivity :
    BaseActivity<ActivityMyAffirmationBinding>(R.layout.activity_my_affirmation) {

    private var job: Job? = null
    private val viewModel: MyAffirmationViewModel by viewModels()
    private val viewModelAffirmation: AffirmationListViewModel by viewModels()
    private val viewModelSeeAll: SeeAllViewModel by viewModels()

    override fun init() {
        viewModelSeeAll.type = 1
        viewModelSeeAll.categoryName = AppConstant.HomeCategory.CREATE_AFFIRMATION
        binding.tbToolbar.tvTitle.text = getString(R.string.my_affirmations)
        viewModel.initializePLayer(this)
        onClick()
        gettingBundleData()

        observe()
        setRecyclerView()
    }

    private fun gettingBundleData() {
        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { type ->
            if (type == AppConstant.CreatedAffirmationEdit.MY_AFFIRMATION.name) {
                setTextColorAndView(
                    binding.tvRecorded,
                    binding.tvWritten,
                    binding.view2,
                    binding.view
                )
                setRecyclerViewRecorded()
                binding.rvRecyclerView.gone()
                binding.rvRecyclerViewRecorded.visible()
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvRecyclerView.adapter = viewModel.adapterWritten
        viewModel.adapterWritten.listener = { position, affirmationId, data ->
            viewModel.position = position
            editAffirmationBottomSheet(
                context = this,
                isDelete = viewModelAffirmation.isSearch.not(),
                isEdit = viewModelAffirmation.isSearch.not(),
                favStatus = data?.favouriteStatus == 1,
                fav = {
                    viewModelAffirmation.affirmationID = affirmationId
                    viewModelAffirmation.isFavourite = data?.favouriteStatus == 1
                    viewModelAffirmation.retryApiRequest(
                        apiName = ApiConstant.FAVOURITE_AFFIRMATION
                    )
                },
                edit = {
                    val bundle = bundleOf(
                        AppConstant.TEXT_AFFIRMATION_DATA to Gson().toJson(data),
                        AppConstant.EDIT to true
                    )
                    activityResult.launch(
                        Intent(this, AddAffirmationActivity::class.java).apply {
                            putExtras(bundle)
                        }
                    )

                },
                share = {

                },
                delete = {
                    viewModelAffirmation.affirmationID = affirmationId
                    viewModelAffirmation.retryApiRequest(
                        apiName = ApiConstant.DELETE_AFFIRMATION
                    )
                    viewModel.isDelete = true
                }, isShare = false
            )
        }
    }

    private fun observe() {
        viewModel.adapterWritten.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbProgress.gone()
                    binding.pbNewData.gone()
                }

                is LoadState.Loading -> {
                    val isListEmpty = viewModel.adapterWritten.snapshot().items.isEmpty()
                    binding.pbProgress.visibility(isVisible = isListEmpty)
                    binding.pbNewData.visibility(isVisible = isListEmpty.not())
                }

                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.adapterWritten.snapshot().items.isEmpty()
                    binding.tvNoAffirmationAdded.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbProgress.gone()
                    binding.pbNewData.gone()
                    binding.tvNoRecordedAffirmation.gone()
                }
            }
        }
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
                    binding.tvNoRecordedAffirmation.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbProgress.gone()
                    binding.pbNewData.gone()
                    binding.tvNoAffirmationAdded.gone()
                }
            }
        }

        getAddedTextAffirmation()

        /** Delete affirmation */
        lifecycleScope.launch {
            viewModelAffirmation.affirmationDelete.collectLatest {
                handleResponse(
                    response = it,
                    context = this@MyAffirmationActivity,
                    success = {
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (viewModel.isDelete) {
                                val currentList =
                                    viewModel.adapterWritten.snapshot().items.toMutableList()
                                currentList.removeAt(viewModel.position)
                                viewModel.adapterWritten.submitData(
                                    pagingData = PagingData.from(
                                        currentList
                                    )
                                )
                                binding.tvNoAffirmationAdded.visibility(isVisible = currentList.isEmpty())
                            } else {
                                val currentList =
                                    viewModel.adapter.snapshot().items.toMutableList()
                                currentList.removeAt(viewModel.position)
                                viewModel.adapter.submitData(
                                    pagingData = PagingData.from(
                                        currentList
                                    )
                                )
                                binding.tvNoRecordedAffirmation.visibility(isVisible = currentList.isEmpty())
                            }
                        }
                    }, error = { message, apiName ->
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

        /** Favourite affirmation */
        lifecycleScope.launch {
            viewModelAffirmation.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@MyAffirmationActivity,
                    success = { _ ->
                        lifecycleScope.launch {
                            val currentList =
                                viewModel.adapterWritten.snapshot().items.toMutableList()
                            currentList[viewModel.position].favouriteStatus =
                                if (viewModelAffirmation.isFavourite) 0 else 1
                            viewModel.adapterWritten.submitData(PagingData.from(currentList.toMutableList()))
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

    override fun onPause() {
        super.onPause()
        if (viewModel.player?.musicPLayingState()?.value == true){
            viewModel.player?.playOrPausePlayer(false)
        }
    }

    private fun setRecyclerViewRecorded() {
        lifecycleScope.launch {
            viewModelSeeAll.seeAllList().collectLatest {
                lifecycleScope.launch {
                    viewModel.adapter.submitData(it)
                }
            }
        }
        binding.rvRecyclerViewRecorded.adapter = viewModel.adapter
        viewModel.adapter.listener = { position, affirmationId, data ->
            viewModel.position = position
            editAffirmationBottomSheet(
                context = this,
                isDelete = viewModelAffirmation.isSearch.not(),
                isEdit = viewModelAffirmation.isSearch.not(),
                fav = {
                },
                edit = {

                    val createAffirmationData = CreateAffirmation(
                        affirmationTitle = data?.title,
                        affirmationDetail = data?.description,
                        isEdit = true,
                        affirmationId = data?.id,
                        audioFileStringUri = data?.audio,
                        transcript = data?.transcript,
                        affirmationBackground = data?.thumbnail,
                        audioDuration = data?.duration?.toLong(),
                        audioType = if (data?.audioType == 1) ApiConstant.AffirmationAudioType.UPLOAD else ApiConstant.AffirmationAudioType.RECORDED,
                        screenType = AppConstant.CreatedAffirmationEdit.MY_AFFIRMATION.name
                    )

                    val bundle = bundleOf(
                        AppConstant.CREATE_AFFIRMATION to Gson().toJson(createAffirmationData),
                        AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.CREATE_AFFIRMATION.name
                    )

                    intent(
                        destination = AddAffirmationActivity::class.java,
                        bundle = bundle
                    )
                },
                share = {
                },
                delete = {
                    viewModelAffirmation.affirmationID = affirmationId
                    viewModelAffirmation.retryApiRequest(
                        apiName = ApiConstant.DELETE_AFFIRMATION
                    )
                    viewModel.isDelete = false
                },
                isShare = false,
                isFav = false
            )
        }
    }

    /**
     * handling on click [onClick]
     * */
    private fun onClick() {
        binding.tvWritten.setOnClickListener {
            binding.tvNoRecordedAffirmation.gone()
            setTextColorAndView(binding.tvWritten, binding.tvRecorded, binding.view, binding.view2)
            binding.rvRecyclerView.adapter = viewModel.adapterWritten
            binding.rvRecyclerView.visible()
            binding.rvRecyclerViewRecorded.gone()
            getAddedTextAffirmation()
            viewModel.player?.releasePlayer()
        }
        binding.tvRecorded.setOnClickListener {
            binding.tvNoAffirmationAdded.gone()
            setTextColorAndView(binding.tvRecorded, binding.tvWritten, binding.view2, binding.view)
            setRecyclerViewRecorded()
            binding.rvRecyclerView.gone()
            binding.rvRecyclerViewRecorded.visible()
        }
        binding.tbToolbar.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setTextColorAndView(
        textView1: AppCompatTextView,
        textView2: AppCompatTextView,
        view1: View,
        view2: View
    ) {
        textView1.setTextColor(
            ContextCompat.getColor(
                this@MyAffirmationActivity,
                R.color.dark_purple_12046A
            )
        )
        textView2.setTextColor(
            ContextCompat.getColor(
                this@MyAffirmationActivity,
                R.color.inactive_purple_7168A6
            )
        )
        view1.setBackgroundColor(
            ContextCompat.getColor(
                this@MyAffirmationActivity,
                R.color.prime_blue_418FF6
            )
        )
        view2.setBackgroundColor(
            ContextCompat.getColor(
                this@MyAffirmationActivity,
                android.R.color.transparent
            )
        )
    }

    private fun getAddedTextAffirmation() {
        if (job != null) job?.cancel()
        job = lifecycleScope.launch {
            ensureActive()
            viewModelAffirmation.getTextAffirmationList().collectLatest { textAffirmationList ->
                lifecycleScope.launch {
                    viewModel.adapterWritten.submitData(textAffirmationList)
                }
            }
        }
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                getAddedTextAffirmation()
            }
        }
}
