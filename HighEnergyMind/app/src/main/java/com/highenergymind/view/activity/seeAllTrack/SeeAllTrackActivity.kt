package com.highenergymind.view.activity.seeAllTrack

import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.SeeAllResponse
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.ActivitySeeAllTrackBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.adapter.FavTrackAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeeAllTrackActivity : BaseActivity<ActivitySeeAllTrackBinding>() {
    val viewModel by viewModels<SeeAllViewModel>()
    val adapter by lazy {
        FavTrackAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_see_all_track
    }

    override fun initView() {
        fullScreenStatusBar()
        clicks()
        setCollectors()
        getBundleData()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                    binding.swipeToRefresh.isRefreshing=(it)
                }
            }
            lifecycleScope.launch {
                markFavResponse.collectLatest {
                    handleResponse(it, {})
                }
            }
            lifecycleScope.launch {
                seeAllResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as SeeAllResponse
                        setAdapter(response.data)
                    })
                }
            }
        }
    }

    private fun getBundleData() {
        if (intent.hasExtra(getString(R.string.see_all))) {
            binding.customTool.tvTitle.text = intent.getStringExtra(getString(R.string.see_all))
            when (intent.getStringExtra(getString(R.string.see_all))) {
                getString(R.string.last_played) -> {

                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.LAST_PLAYER.value
                }

                getString(R.string.recommendation) -> {
                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.RECOMMENDATION.value

                }

                getString(R.string.playlist_of_the_month) -> {
                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.PLAYLIST_MONTH.value

                }

                getString(R.string.popular_tracks) -> {
                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.POPULAR.value

                }

                getString(R.string.popular_content) -> {
                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.POPULAR.value

                }

                getString(R.string.curated_to_you) -> {
                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.LAST_PLAYER.value

                }

                else -> {
                    viewModel.map[ApiConstant.TYPE] = AppConstant.SEE_ALL.LAST_PLAYER.value

                }
            }
            viewModel.map[ApiConstant.KEYWORD] = binding.searchll.text?.trim()?.toString() ?: ""
            viewModel.seeAllApi()
        }
    }

    private fun clicks() {
        binding.apply {
            swipeToRefresh.setOnRefreshListener {
                getBundleData()
            }
            searchll.addTextChangedListener {
                getBundleData()
            }
            customTool.ivBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setAdapter(data: List<TrackOb>) {
        binding.apply {
            rvTrack.adapter = adapter.also {
                it.callBack = { item, pos, type ->
                    markFavApi(item)
                }
            }
            adapter.submitList(data)
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
}