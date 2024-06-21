package com.highenergymind.view.activity.search

import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.CheckModel
import com.highenergymind.data.SearchResponse
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.ActivitySearchBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible
import com.highenergymind.view.adapter.FavTrackAdapter
import com.highenergymind.view.sheet.category.CategorySheet
import com.highenergymind.view.sheet.contenttype.ContentTypeSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private val trackAdapter by lazy {
        FavTrackAdapter(true)
    }
    var type = AppConstant.TYPE_TRACK
    private var categoryModel = mutableListOf<CheckModel>()

    val viewModel by viewModels<SearchViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_search
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolBar()
        setCollectors()
        onClick()

        callSearchApi()
    }

    private fun callSearchApi() {
        viewModel.apply {
            map.clear()
            map[ApiConstant.KEYWORD] = binding.etSearch.text?.trim()?.toString() ?: ""
            map[ApiConstant.CATEGORY] =
                categoryModel.filter { it.isChecked }.map { it.id }.joinToString(",")
            map[ApiConstant.TYPE] = type
            getSearchApi()

        }
    }

    private fun setCollectors() {

        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                    binding.swipeToRefresh.isRefreshing = it
                }
            }
            lifecycleScope.launch {
                searchResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as SearchResponse
                        binding.rvList.adapter = trackAdapter.also {
                            it.callBack = { item, pos, type ->
                                markFavApi(item)
                            }
                        }
                        trackAdapter.submitList(response.data.trackList)
                        if (response.data.trackList.isEmpty()) binding.tvNoDataFound.visible() else binding.tvNoDataFound.gone()
                    })
                }
            }
        }
    }

    private fun markFavApi(item: TrackOb) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.ID] = item.id
            map[ApiConstant.FAVOURITE] = item.isFav ?: false
            map[ApiConstant.TYPE] = AppConstant.TYPE_TRACK
            markFav()
        }
    }
    private fun setToolBar() {
        binding.apply {
            cutomTool.tvTitle.text = getString(R.string.search)
        }
    }

    private fun onClick() {


        binding.apply {
            swipeToRefresh.setOnRefreshListener {
                callSearchApi()
            }

            etSearch.addTextChangedListener {
                it?.trim()?.toString()?.let { str ->
                    callSearchApi()
                }
            }
            cutomTool.ivBack.setOnClickListener {
                finish()
            }
            conentTV.setOnClickListener {
                ContentTypeSheet(type).also {
                    it.callBack = { t ->
                        type = t
                    }
                    it.show(supportFragmentManager, "contentType")
                }
            }

            llCategory.setOnClickListener {
                supportFragmentManager.let {
                    CategorySheet(categoryList = categoryModel).also { sheet ->
                        sheet.callBack = {
                            categoryModel = it
                            callSearchApi()
                        }
                        sheet.show(it, "")
                    }
                }
            }
        }
    }
}