package com.mindbyromanzanoni.view.fragment.resource

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseFragment
import com.mindbyromanzanoni.data.response.resource.ResourceCategoryList
import com.mindbyromanzanoni.databinding.FragmentResourceBinding
import com.mindbyromanzanoni.databinding.RowitemResourcesBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.resourceListing.ResourceListingActivity
import com.mindbyromanzanoni.view.activity.searching.SearchCatOrSubCatActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResourceFragment : BaseFragment<FragmentResourceBinding>(FragmentResourceBinding::inflate) {
    private val viewModal: HomeViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }
    private fun initData() {
        setViewModel()
        initMeditationRecyclerView()
        observeDataFromViewModal()
        apiHit()
        onClickListener()
    }
    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitResourceCategoryApi()
        }
    }
    private fun setViewModel() {
        binding.viewModel = viewModal
    }
    private fun onClickListener() {
        binding.apply {
            binding.apply {
                ivImg.setOnClickListener {
                    etSearchMeditation.text?.clear()
                }
                etSearchMeditation.setOnClickListener {
                    val bundle = bundleOf(AppConstants.SCREEN_TYPE to AppConstants.RESOURCE_SCREEN)
                    requireActivity().launchActivity<SearchCatOrSubCatActivity>(0,bundle) {  }
                }
            }
        }
    }
    /** set recycler view Resources  List */
    private fun initMeditationRecyclerView() {
        binding.rvResource.adapter = resourceListAdapter
    }
    private val resourceListAdapter =
        object : GenericAdapter<RowitemResourcesBinding, ResourceCategoryList>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.rowitem_resources
            }
            override fun onBindHolder(
                holder: RowitemResourcesBinding,
                dataClass: ResourceCategoryList,
                position: Int) {
                holder.apply {
                    tvName.text = dataClass.resourceTypeMain
                    ivImage.setImageFromUrl(dataClass.categoryImage, progressBar)
                    cvResourcesItem.setOnClickListener {
                        val bundle = bundleOf(
                            AppConstants.RESOURCE_TYPE_ID to dataClass.resourceTypeId.toString(),
                            AppConstants.CAT_NAME to dataClass.resourceTypeMain
                        )
                        requireActivity().launchActivity<ResourceListingActivity>(0, bundle) { }
                    }
                }

            }
        }
    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.resourceListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if (data.data?.isEmpty() == true) {
                                binding.noDataFound.visible()
                            } else {
                                binding.noDataFound.gone()
                                resourceListAdapter.submitList(data.data ?: arrayListOf())
                            }
                        } else {
                            showErrorSnack(requireActivity(), data?.message ?: "")
                        }
                    }
                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(requireActivity(), msg) }
                    }
                }
            }
        }
        viewModal.showLoading.observe(requireActivity()) {
            binding.apply {
                mainLayoutShimmer.shimmerAnimationEffect(it)
            }
        }
    }
}