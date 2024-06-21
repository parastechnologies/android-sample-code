package com.mindbyromanzanoni.view.fragment.edification

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseFragment
import com.mindbyromanzanoni.data.response.edification.EdificationCatListResponse
import com.mindbyromanzanoni.databinding.FragmentEdificationBinding
import com.mindbyromanzanoni.databinding.RowitemEdificationBinding
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
import com.mindbyromanzanoni.view.activity.edificationCategoryList.EdificationCategoryListActivity
import com.mindbyromanzanoni.view.activity.searching.SearchCatOrSubCatActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EdificationFragment :
    BaseFragment<FragmentEdificationBinding>(FragmentEdificationBinding::inflate) {
    private val viewModal: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        setViewModel()
        clickListener()
        initEdificationRecyclerView()
        observeDataFromViewModal()
        apiHit()
    }

    private fun setViewModel() {
        binding.viewModel = viewModal
    }

    private fun clickListener() {
        binding.apply {
            ivImg.setOnClickListener {
                etSearchMeditation.text?.clear()
            }
            etSearchMeditation.setOnClickListener {
                val bundle = bundleOf(AppConstants.SCREEN_TYPE to AppConstants.EDIFICATION_SCREEN)
                requireActivity().launchActivity<SearchCatOrSubCatActivity>(0,bundle) {  }
            }
        }
    }


    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitEdificationCategoryListApi()
        }
    }

    /** set recycler view Edification  List */
    private fun initEdificationRecyclerView() {
        binding.rvEdification.adapter = edificationListAdapter
    }

    private val edificationListAdapter =
        object : GenericAdapter<RowitemEdificationBinding, EdificationCatListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.rowitem_edification
            }

            override fun onBindHolder(
                holder: RowitemEdificationBinding,
                dataClass: EdificationCatListResponse,
                position: Int
            ) {
                holder.apply {

                    tvEdificationName.text = dataClass.ediCategoryName
                    ivImage.setImageFromUrl(dataClass.edificationCategoryImage, progressBar)

                    cvEdificationItem.setOnClickListener {
                        val bundle = bundleOf(
                            AppConstants.EDIFICATION_CAT_ID to dataClass.edificationId.toString(),
                            AppConstants.CAT_NAME to dataClass.ediCategoryName
                        )
                        requireActivity().launchActivity<EdificationCategoryListActivity>(0, bundle) { }
                    }
                }

            }
        }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.edificationCategoryListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if (data.data?.isEmpty() == true) {
                                binding.noDataFound.visible()
                            } else {
                                binding.noDataFound.gone()
                                edificationListAdapter.submitList(data.data ?: arrayListOf())
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