package com.highenergymind.view.activity.recent

import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.RecentListResponse
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.ActivityRecentPlayBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible
import com.highenergymind.view.adapter.FavTrackAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecentPlayActivity : BaseActivity<ActivityRecentPlayBinding>() {
    private val trackAdapter by lazy {
        FavTrackAdapter(true)
    }
    val viewModel by viewModels<RecentPlayViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_recent_play
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolBar()
        setCollectors()
        clicks()
        callRecentApi()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                   binding.swipeToRefresh.isRefreshing=it
                }
            }
            lifecycleScope.launch {
                getRecentResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as RecentListResponse
                        setAdapter(response.data)
                        if (response.data.isEmpty()) binding.tvNoDataFound.visible() else binding.tvNoDataFound.gone()
                    })
                }
            }
        }
    }

    private fun setAdapter(data: List<TrackOb>) {
        binding.apply {
            rvList.adapter = trackAdapter
            trackAdapter.submitList(data)
        }
    }

    private fun clicks() {
        binding.apply {
            binding.swipeToRefresh.setOnRefreshListener {
                callRecentApi()
            }
            etSearch.addTextChangedListener {
                it?.trim()?.toString()?.let { str ->
                    callRecentApi()
                }
            }
        }
    }

    private fun callRecentApi() {
        viewModel.apply {
            map.clear()
            map[ApiConstant.KEYWORD] = binding.etSearch.text?.trim()?.toString() ?: ""
            getRecentApi()
        }
    }

    private fun setToolBar() {
        binding.apply {
            cutomTool.tvTitle.text = getString(R.string.recent)
            cutomTool.ivBack.setOnClickListener {
                finish()
            }
        }
    }

}