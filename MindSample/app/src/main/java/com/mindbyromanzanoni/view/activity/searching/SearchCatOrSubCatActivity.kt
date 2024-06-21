package com.mindbyromanzanoni.view.activity.searching

import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.search.SearchCatOrSubCatListResponse
import com.mindbyromanzanoni.databinding.ActivitySearchCatOrSubCatBinding
import com.mindbyromanzanoni.databinding.ItemSearchCatOrSubCatBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.setSearchTextWatcher
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.edificationCategoryList.EdificationCategoryListActivity
import com.mindbyromanzanoni.view.activity.edificationVideoPlayer.EdificationVideoPlayerActivity
import com.mindbyromanzanoni.view.activity.meditationCategoryList.MeditationCategoryListActivity
import com.mindbyromanzanoni.view.activity.nowPlaying.NowPlayingActivity
import com.mindbyromanzanoni.view.activity.resource.ResourceDetailActivity
import com.mindbyromanzanoni.view.activity.resourceListing.ResourceListingActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchCatOrSubCatActivity : BaseActivity<ActivitySearchCatOrSubCatBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    override fun getLayoutRes(): Int {
        return R.layout.activity_search_cat_or_sub_cat
    }

    override fun initView() {
        setToolbar()
        getIntentData()
        initMeditationRecyclerView()
        observeDataFromViewModal()
        getWatcherSearchMeditation()
    }

    override fun viewModel() {}
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            val screenType = intent.getString(AppConstants.SCREEN_TYPE).toString()
            if (screenType == AppConstants.MEDITATION_SCREEN) {
                viewModal.typeId.set(2)
            } else if (screenType == AppConstants.EDIFICATION_SCREEN) {
                viewModal.typeId.set(1)
            } else if (screenType == AppConstants.RESOURCE_SCREEN) {
                viewModal.typeId.set(3)
            }
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.search)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
        binding.ivImg.setOnClickListener {
            if (binding.etSearchMeditation.text?.trim().toString().isNotEmpty()) {
                binding.etSearchMeditation.setText("")
            }
        }
    }

    private fun getWatcherSearchMeditation() {
        // Call the extension function to set up the text watcher
        binding.etSearchMeditation.setSearchTextWatcher { query ->
            if (query.isEmpty()) {
                binding.ivImg.setImageResource(R.drawable.search)
            } else {
                binding.ivImg.setImageResource(R.drawable.rating_close)
            }
            viewModal.searchKeyword.set(query)
            RunInScope.mainThread {
                binding.noDataFound.gone()
            }
            RunInScope.ioThread {
                viewModal.hitSearchApiAccordingToCatOrSubCatApi()
            }
        }
    }

    /** set recycler view Meditation  List */
    private fun initMeditationRecyclerView() {
        binding.rvMeditation.adapter = meditationListAdapter
    }

    /**
     * dataClass.typeId
     * 1-- for main cat
     * 2-- for video or audio
     *
     * viewModal.typeId.get()
     * 1- Meditation
     * 2- Edification
     * 3- Resources
     *
     * */
    private val meditationListAdapter =
        object : GenericAdapter<ItemSearchCatOrSubCatBinding, SearchCatOrSubCatListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_search_cat_or_sub_cat
            }

            override fun onBindHolder(
                holder: ItemSearchCatOrSubCatBinding,
                dataClass: SearchCatOrSubCatListResponse,
                position: Int) {
                holder.apply {
                    if (viewModal.typeId.get() == 1 ||viewModal.typeId.get() == 2) {
                        if (dataClass.typeId==2 ) tvPlay.visible() else tvPlay.gone()
                    } else {
                        tvPlay.gone()
                    }
                    if (dataClass.typeId == 1) {
                        tvMeditationName.text = dataClass.categoryName ?: dataClass.title
                    } else {
                        tvMeditationName.text = dataClass.title ?: dataClass.categoryName
                    }
                    ivImage.setImageFromUrl(R.drawable.placeholder_mind, dataClass.thumbImage)
                    root.setOnClickListener {
                        if (dataClass.typeId == 1) {
                            if (viewModal.typeId.get() == 1) {
                                val bundle = bundleOf(
                                    AppConstants.EDIFICATION_CAT_ID to dataClass.categoryId.toString(),
                                    AppConstants.CAT_NAME to dataClass.categoryName
                                )
                                launchActivity<EdificationCategoryListActivity>(0, bundle) { }
                            } else if (viewModal.typeId.get() == 3) {
                                val bundle = bundleOf(
                                    AppConstants.RESOURCE_TYPE_ID to dataClass.categoryId.toString(),
                                    AppConstants.CAT_NAME to dataClass.categoryName
                                )
                                launchActivity<ResourceListingActivity>(0, bundle) { }
                            } else if (viewModal.typeId.get() == 2) {
                                val bundle = bundleOf(
                                    AppConstants.MEDITATION_CAT_ID to dataClass.categoryId.toString(),
                                    AppConstants.CAT_NAME to dataClass.categoryName
                                )
                                launchActivity<MeditationCategoryListActivity>(0, bundle) { }
                            }
                        } else if (dataClass.typeId == 2) {
                            if (viewModal.typeId.get() == 1) {
                                val dataJson = Gson().toJson(dataClass)
                                val bundle = bundleOf(
                                    AppConstants.EDIFICATION_CAT_ID to dataClass.categoryId,
                                    AppConstants.EDIFICATION_DATA to dataJson
                                )
                                launchActivity<EdificationVideoPlayerActivity>(0, bundle) { }
                            } else if (viewModal.typeId.get() == 3) {
                                val dataJson = Gson().toJson(dataClass)
                                val bundle = bundleOf(AppConstants.RESOURCE_DETAILS to dataJson)
                                launchActivity<ResourceDetailActivity>(0, bundle) { }
                            } else if (viewModal.typeId.get() == 2) {
                                val dataJson = Gson().toJson(dataClass)
                                val bundle = bundleOf(
                                    AppConstants.SCREEN_TYPE to AppConstants.MEDITATION_SCREEN,
                                    AppConstants.MEDIATION_DETAILS to dataJson
                                )
                                launchActivity<NowPlayingActivity>(0, bundle) { }
                            }
                        }
                    }
                }
            }
        }
    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.searchCatOrSubCatSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if (data.data?.isEmpty() == true) {
                                binding.noDataFound.visible()
                            } else {
                                binding.noDataFound.gone()
                            }
                            meditationListAdapter.submitList(data.data ?: arrayListOf())
                        } else {
                            showErrorSnack(this@SearchCatOrSubCatActivity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(
                                this@SearchCatOrSubCatActivity,
                                msg
                            )
                        }
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    if (isResponse.data?.data.isNullOrEmpty()) {
                        binding.noDataFound.visible()
                    } else {
                        binding.noDataFound.gone()
                    }
                }
            }
        }
        viewModal.showLoading.observe(this@SearchCatOrSubCatActivity) {
            binding.apply {
                if (it) {
                    binding.rvMeditation.gone()
                    binding.mainShimmerLayout.visible()
                } else {
                    binding.rvMeditation.visible()
                    binding.mainShimmerLayout.gone()
                }
            }
        }
    }
}