package com.mindbyromanzanoni.view.fragment.meditations

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseFragment
import com.mindbyromanzanoni.data.response.meditation.MeditationCatListResponse
import com.mindbyromanzanoni.databinding.FragmentMeditationsBinding
import com.mindbyromanzanoni.databinding.RowitemMeditationBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.constant.AppConstants.CAT_NAME
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.setSearchTextWatcher
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.meditationCategoryList.MeditationCategoryListActivity
import com.mindbyromanzanoni.view.activity.searching.SearchCatOrSubCatActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeditationsFragment : BaseFragment<FragmentMeditationsBinding>(FragmentMeditationsBinding::inflate) {
    private val viewModal: HomeViewModel by viewModels()
    private var filteredList: ArrayList<MeditationCatListResponse> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        setViewModel()
        clickListener()
        apiHit()
        observeDataFromViewModal()
    }

    private fun getWatcherSearchMeditation() {
        binding.etSearchMeditation.isEnabled = true
        // Call the extension function to set up the text watcher
        binding.etSearchMeditation.setSearchTextWatcher { query ->
            if (query.isEmpty()){
                binding.ivImg.setImageResource(R.drawable.search)
            }else{
                viewModal.searchKeyword.set(query)
                viewModal.typeId.set(1)
                RunInScope.ioThread {
                    viewModal.hitSearchApiAccordingToCatOrSubCatApi()
                }
                binding.ivImg.setImageResource(R.drawable.rating_close)
            }
            filter(query)
        }
    }


    private fun clickListener() {
        binding.apply {
            ivImg.setOnClickListener {
                etSearchMeditation.text?.clear()
            }
            etSearchMeditation.setOnClickListener {
                val bundle = bundleOf(AppConstants.SCREEN_TYPE to AppConstants.MEDITATION_SCREEN)
                requireActivity().launchActivity<SearchCatOrSubCatActivity>(0,bundle) {  }
            }
        }
    }


    private fun setViewModel() {
        binding.viewModel = viewModal
    }


    /** Function to filter data based on search query*/
    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val list: ArrayList<MeditationCatListResponse> = if (query.isEmpty()) {
            filteredList
        } else {
            filteredList.filter { item ->
                item.categoryName.contains(query, ignoreCase = true)
            } as ArrayList<MeditationCatListResponse>
        }
        if(list.isEmpty()){
            binding.noDataFound.visible()
            meditationListAdapter.submitList(list)
        }else{
            binding.noDataFound.gone()
            meditationListAdapter.submitList(list)
        }
    }

    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitMeditationCatListApi()
        }
    }

    /** set recycler view Meditation  List */
    private fun initMeditationRecyclerView() {
        binding.rvMeditation.adapter = meditationListAdapter
        meditationListAdapter.submitList(filteredList)
    }

    private val meditationListAdapter = object : GenericAdapter<RowitemMeditationBinding, MeditationCatListResponse>() {
        override fun getResourceLayoutId(): Int {
            return R.layout.rowitem_meditation
        }

        override fun onBindHolder(holder: RowitemMeditationBinding, dataClass: MeditationCatListResponse, position: Int) {
            holder.apply {
                tvMeditationName.text = dataClass.categoryName
                ivImage.setImageFromUrl(dataClass.categoryImage,progressBar)

                cvMeditationItem.setOnClickListener {
                    val bundle = bundleOf(AppConstants.MEDITATION_CAT_ID to dataClass.medidationCatId.toString(), CAT_NAME to dataClass.categoryName)
                    requireActivity().launchActivity<MeditationCategoryListActivity>(0,bundle) { }
                }
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.meditationCatListSharedFlow.collectLatest {isResponse->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            filteredList = data.data
                            if(data.data.isEmpty()){
                                binding.apply {
                                    noDataFound.visible()
                                    searchLayout.gone()
                                }
                            }else{
                                binding.apply {
                                    searchLayout.visible()
                                    noDataFound.gone()
                                }
                                initMeditationRecyclerView()
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