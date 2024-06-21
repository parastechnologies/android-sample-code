package com.highenergymind.view.fragment.favoritePager

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.AffDay
import com.highenergymind.data.FavResponse
import com.highenergymind.databinding.FragmentFavAffirmationBinding
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible
import com.highenergymind.view.adapter.FavAffirmationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint

class FavAffirmationFragment : BaseFragment<FragmentFavAffirmationBinding>() {
    val viewModel by viewModels<FavViewModel>()
    val adapter by lazy {
        FavAffirmationAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_fav_affirmation
    }

    override fun initViewWithData() {
        setCollectors()
        onClick()
        viewModel.getFavouriteList()


    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    mBinding.swipeToRefresh.isRefreshing = it
                }

            }
            viewLifecycleOwner.lifecycleScope.launch {
                favResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as FavResponse
                        setAdapter(response.data.affirmationList, response.data.backgroundAffImg)
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
                viewModel.keyword = it?.toString()?.trim() ?: ""
                viewModel.getFavouriteList()
            }
        }
    }

    private fun setAdapter(data: List<AffDay>, backgroundAffImg: String?) {
        mBinding.apply {
            if (data.isEmpty()) {
                tvNoDataFound.visible()
            } else {
                tvNoDataFound.gone()
            }
            adapter.backImage = backgroundAffImg
            rvAffirmation.adapter = adapter
            adapter.submitList(data)
        }
    }

}