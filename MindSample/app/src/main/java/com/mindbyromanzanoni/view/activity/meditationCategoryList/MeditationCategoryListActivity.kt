package com.mindbyromanzanoni.view.activity.meditationCategoryList

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeListResponse
import com.mindbyromanzanoni.databinding.ActivityMeditationCategoryListBinding
import com.mindbyromanzanoni.databinding.RowitemMeditationlistBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.constant.AppConstants.MEDIATION_DETAILS
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.nowPlaying.NowPlayingActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MeditationCategoryListActivity : BaseActivity<ActivityMeditationCategoryListBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@MeditationCategoryListActivity
    private var filteredList: ArrayList<MeditationTypeListResponse> = arrayListOf()
    private var catName = ""

    override fun getLayoutRes() = R.layout.activity_meditation_category_list

    override fun initView() {
        getIntentData()
        setToolbar()
        initData()
        getWatcherSearchMeditation()
        clickListener()
    }

    private fun clickListener() {
        binding.apply {
            ivImg.setOnClickListener {
                etSearchMeditation.text?.clear()
            }
        }
    }


    /** Text watcher when user search Meditation list*/
    private fun getWatcherSearchMeditation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModal.meditationSearchText.set(p0.toString())
                filter(p0.toString())
            }
        }
        binding.etSearchMeditation.addTextChangedListener(watcher)
    }

    /** get Intent data from
     * MeditationsFragment
     * */
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            viewModal.categoryId.set(intent.getString(AppConstants.MEDITATION_CAT_ID).toString())
            catName = intent.getString(AppConstants.CAT_NAME).toString()
        }
    }

    /** Function to filter data based on search query*/
    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val list: ArrayList<MeditationTypeListResponse>
        if (query.isEmpty()) {
            list = filteredList
            binding.ivImg.setImageResource(R.drawable.search)
        } else {
            list = filteredList.filter { item ->
                item.title.contains(query, ignoreCase = true)
            } as ArrayList<MeditationTypeListResponse>
            binding.ivImg.setImageResource(R.drawable.rating_close)
        }
        categoryMeditationListAdapter.submitList(list)
    }


    /** set toolbar*/
    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = catName
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }


    private fun initData() {
        apiHit()
        observeDataFromViewModal()
    }

    /** Meditation List api Call*/
    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitMeditationTypeListApi()
        }
    }

    /** set recycler view Meditation  List */
    private fun initMeditationRecyclerView() {
        binding.rvCategoryMedification.adapter = categoryMeditationListAdapter
        categoryMeditationListAdapter.submitList(filteredList)
    }

    private val categoryMeditationListAdapter =
        object : GenericAdapter<RowitemMeditationlistBinding, MeditationTypeListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.rowitem_meditationlist
            }

            override fun onBindHolder(
                holder: RowitemMeditationlistBinding,
                dataClass: MeditationTypeListResponse,
                position: Int
            ) {
                holder.apply {
                    tvName.text = dataClass.title
                    tvDec.text = dataClass.content
                    tvDuration.text = dataClass.duration
                    ivImage.setImageFromUrl(dataClass.videoThumbImage, progressBar)

                    root.setOnClickListener {
                        val dataJson = Gson().toJson(dataClass)
                        val bundle = bundleOf(
                            AppConstants.SCREEN_TYPE to AppConstants.MEDITATION_SCREEN,
                            MEDIATION_DETAILS to dataJson
                        )
                        launchActivity<NowPlayingActivity>(0, bundle) { }
                    }
                }
            }
        }


    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.meditationTypeListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            filteredList = data.data
                            if (data.data.isEmpty()) {
                                binding.noDataFound.visible()
                            } else {
                                binding.noDataFound.gone()
                                initMeditationRecyclerView()
                            }
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModal.searchMeditationSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
//                            categoryMeditationListAdapter.submitList(data.data)
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }

        viewModal.showLoading.observe(activity) {
            if (it) {
                binding.apply {
                    rvCategoryMedification.gone()
                    shimmerCommentList.shimmerAnimationEffect(it)
                }
            } else {
                binding.apply {
                    rvCategoryMedification.visible()
                    shimmerCommentList.shimmerAnimationEffect(it)
                }
            }
        }
    }
}