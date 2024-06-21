package com.highenergymind.view.fragment.home.search

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.ExploreSectionData
import com.highenergymind.data.ExploreSectionResponse
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.FragmentSearchBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.favorite.FavoriteActivity
import com.highenergymind.view.activity.profile.ProfileActivity
import com.highenergymind.view.activity.recent.RecentPlayActivity
import com.highenergymind.view.activity.search.SearchActivity
import com.highenergymind.view.activity.seeAllTrack.SeeAllTrackActivity
import com.highenergymind.view.adapter.HomeAffirmationAdapter
import com.highenergymind.view.sheet.empoweringAffirmationCategory.EmpoweringAffirmCategorySheet
import com.highenergymind.view.sheet.trackmusicsheet.TrackMusicSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    val viewModel by viewModels<SearchFragmentViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val popularAdapter by lazy { HomeAffirmationAdapter(true) }
    private val curatedAdapter by lazy { HomeAffirmationAdapter(true) }
    override fun getLayoutRes(): Int {
        return R.layout.fragment_search
    }

    override fun initViewWithData() {
        setCollectors()
        setAdapter()
        setLocalData()
        clicks()
        if (curatedAdapter.currentList.isEmpty()) {
            viewModel.getSearchSectionData()
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    mBinding.swipeToRefresh.isRefreshing = it
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                searchSectionResponse.collectLatest {
                    handleResponse(it, { resp ->
                        val response = resp as ExploreSectionResponse
                        setDataOnAdapters(response.data)
                    })
                }
            }
        }
    }

    private fun setDataOnAdapters(data: ExploreSectionData) {

        mBinding.apply {
            curatedAdapter.submitList(data.curated)
            popularAdapter.submitList(data.popular)
        }
    }

    private fun setLocalData() {
        val userData = sharedPrefs.getUserData()

        mBinding.apply {
            ivImage.glideImage(userData?.userImg)
        }
    }

    private fun clicks() {
        mBinding.apply {
            swipeToRefresh.setOnRefreshListener {
                viewModel.getSearchSectionData()
            }
            cvImage.setOnClickListener {
                requireContext().intentComponent(ProfileActivity::class.java)
            }
            cvRecent.setOnClickListener {
                requireContext().intentComponent(RecentPlayActivity::class.java)
            }

            tvSeeAllPopular.setOnClickListener {
                requireContext().intentComponent(SeeAllTrackActivity::class.java, Bundle().also {
                    it.putString(getString(R.string.see_all), tvPopularContent.text.toString())
                })
            }

            tvSeeAllCuratedYou.setOnClickListener {
                requireContext().intentComponent(SeeAllTrackActivity::class.java, Bundle().also {
                    it.putString(getString(R.string.see_all), tvCuratedYou.text.toString())
                })
            }

            cvFavorite.setOnClickListener {
                requireContext().intentComponent(FavoriteActivity::class.java)
            }
            cvCategory.setOnClickListener {
                EmpoweringAffirmCategorySheet().also {
                    it.callBack = {
                        requireContext().intentComponent(
                            SeeAllTrackActivity::class.java,
                            Bundle().also { bnd ->
                                bnd.putString(
                                    getString(R.string.see_all),
                                    requireContext().getString(R.string.search)
                                )
                            })
                    }
                }.show(childFragmentManager, "")
            }
            cvMusic.setOnClickListener {
                TrackMusicSheet().also {
                    it.isMusicDetailShow = true
                }.show(childFragmentManager, "")
            }

            searchLL.setOnClickListener {
                requireActivity().intentComponent(SearchActivity::class.java, null)
            }
        }
    }

    private fun setAdapter() {
        mBinding.apply {
            rvPopularContent.adapter = popularAdapter.also {
                it.callBack = { item, pos, type ->
                    markFavApi(item)
                }
            }
            rvCuratedYou.adapter = curatedAdapter.also {
                it.callBack = { item, pos, type ->
                    markFavApi(item)
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
}