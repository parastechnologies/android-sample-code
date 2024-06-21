package com.mindbyromanzanoni.view.activity.edificationCategoryList

import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.edification.EdificationTypeListResponse
import com.mindbyromanzanoni.databinding.ActivityEdificationCategoryListBinding
import com.mindbyromanzanoni.databinding.RowitemEdificationlistBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.edificationVideoPlayer.EdificationVideoPlayerActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EdificationCategoryListActivity : BaseActivity<ActivityEdificationCategoryListBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@EdificationCategoryListActivity
    private lateinit var categoryId: String
    private lateinit var catName: String
    private var list : ArrayList<EdificationTypeListResponse> = arrayListOf()

    override fun getLayoutRes() = R.layout.activity_edification_category_list

    override fun initView() {
        getIntentData()
        setToolbar()
        initData()
    }

    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            categoryId = intent.getString(AppConstants.EDIFICATION_CAT_ID).toString()
            catName = intent.getString(AppConstants.CAT_NAME).toString()
            viewModal.categoryId.set(categoryId)
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
            viewModal.hitEdificationListApi()
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = catName
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    /** set recycler view Meditation  List */
    private fun initEdificationTypeRecyclerView(data: ArrayList<EdificationTypeListResponse>) {
        binding.rvCategoryEdification.adapter = categoryEdificationListAdapter
        categoryEdificationListAdapter.submitList(data)
    }

    private val categoryEdificationListAdapter =
        object : GenericAdapter<RowitemEdificationlistBinding, EdificationTypeListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.rowitem_edificationlist
            }

            override fun onBindHolder(
                holder: RowitemEdificationlistBinding,
                dataClass: EdificationTypeListResponse,
                position: Int
            ) {
                holder.apply {

                    tvName.text = dataClass.title
                    tvDec.text = dataClass.content
                    tvDuration.text = dataClass.duration
                    ivImage.setImageFromUrl(dataClass.videoThumbImage, progressBar)
                    tvDuration.text = dataClass.duration

                    root.setOnClickListener {
                        val dataJson = Gson().toJson(dataClass)
                        val dataList = Gson().toJson(list)
                        val bundle = bundleOf(
                            AppConstants.EDIFICATION_CAT_ID to categoryId,
                            AppConstants.EDIFICATION_DATA to dataJson,
                            AppConstants.EDIFICATION_TYPE_LIST to dataList
                        )
                        launchActivity<EdificationVideoPlayerActivity>(0, bundle) { }
                    }
                }
            }
        }
    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.edificationListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if (data.data.isEmpty()) {
                                binding.noDataFound.visible()
                            } else {
                                binding.noDataFound.gone()
                                list = data.data
                                initEdificationTypeRecyclerView(data.data)
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
        viewModal.showLoading.observe(activity) {
            if (it) {
                binding.apply {
                    rvCategoryEdification.gone()
                    shimmerCommentList.shimmerAnimationEffect(it)
                }
            } else {
                binding.apply {
                    rvCategoryEdification.visible()
                    shimmerCommentList.shimmerAnimationEffect(it)
                }
            }
        }
    }
}