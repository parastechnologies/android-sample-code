package com.highenergymind.view.fragment.favoritePager

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.FavResponse
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.FragmentFavTrackBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.search.SearchActivity
import com.highenergymind.view.adapter.FavTrackAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavTrackFragment : BaseFragment<FragmentFavTrackBinding>() {
    val viewModel by viewModels<FavViewModel>()

    val adapter by lazy {
        FavTrackAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_fav_track
    }

    override fun initViewWithData() {
        setCollectors()
        onClick()
        viewModel.type = AppConstant.TYPE_TRACK
        viewModel.getFavouriteList()

    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    mBinding.swipeToRefresh.isRefreshing=it
                }

            }
            viewLifecycleOwner.lifecycleScope.launch {
                markFavResponse.collectLatest {
                    handleResponse(it, {})
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                favResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as FavResponse
                        setAdapter(response.data.trackList)
                    })
                }
            }
        }
    }

    private fun onClick() {
        mBinding.apply {
            swipeToRefresh.setOnRefreshListener {
                viewModel.getFavouriteList()
            }

            etSearch.addTextChangedListener {
                viewModel.keyword=it?.toString()?.trim()?:""
                viewModel.getFavouriteList()
            }
        }
    }

    private fun setAdapter(data: List<TrackOb>) {
        mBinding.apply {
            if (data.isEmpty()){
                tvNoDataFound.visible()
            }else{
                tvNoDataFound.gone()
            }
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