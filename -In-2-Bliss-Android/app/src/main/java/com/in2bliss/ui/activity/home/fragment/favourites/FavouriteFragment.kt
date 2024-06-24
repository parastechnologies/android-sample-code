package com.in2bliss.ui.activity.home.fragment.favourites

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.FragmentFavouriteBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.home.notification.NotificationActivity
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.ui.activity.home.profileManagement.favourites.FavouritesVM
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.getRealCategoryType
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment : BaseFragment<FragmentFavouriteBinding>(
    layoutInflater = FragmentFavouriteBinding::inflate
) {

    private var job: Job? = null
    private val viewModel: FavouritesVM by viewModels()

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivNotification.setOnClickListener {
            requireActivity().intent(NotificationActivity::class.java)
        }
        setProfile()
        recyclerView()
        observer()
    }

    private fun setProfile() {
        binding.ivProfile.glide(
            requestManager = requestManager,
            image = BuildConfig.PROFILE_BASE_URL.plus(sharedPreference.userData?.data?.profilePicture),
            placeholder = R.drawable.ic_user_placholder,
            error = R.drawable.ic_user_placholder
        )
    }


    private fun observer() {
        viewModel.favouriteAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbProgress.gone()
                    binding.pbSeeAllNewData.gone()
                }

                is LoadState.Loading -> {
                    val isEmptyList = viewModel.favouriteAdapter.snapshot().isEmpty()
                    binding.pbProgress.visibility(isVisible = isEmptyList)
                    binding.pbSeeAllNewData.visibility(isVisible = isEmptyList.not())
                }

                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.favouriteAdapter.snapshot().items.isEmpty()
                    binding.tvNoDatFound.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbProgress.gone()
                    binding.pbSeeAllNewData.gone()
                }
            }
        }
        getFavourites()

        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = { _ ->
                        favouriteNotify(
                            fav = if (viewModel.isFav == 0) 1 else 0
                        )
                    },
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }
    }

    private fun getFavourites() {

        viewModel.favouriteAdapter.categoryType = viewModel.getCategoryType()
        viewModel.favouriteAdapter.type = viewModel.categoryType
        if (job != null) job?.cancel()
        job = lifecycleScope.launch {
            viewModel.favouritesList().collectLatest { favouritesList ->
                viewModel.favouriteAdapter.submitData(favouritesList)
            }
        }
    }

    private fun recyclerView() {
        binding.rvRecyclerView.adapter = viewModel.favouritesTypeAdapter
        (binding.rvRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        viewModel.favouritesTypeAdapter.submitList(viewModel.getTypeList(requireActivity()))
        viewModel.favouritesTypeAdapter.onClick = { favouritesType, type ->
            viewModel.categoryType = favouritesType
            viewModel.favouriteType = type
            getFavourites()
        }

        binding.rvFavourites.adapter = viewModel.favouriteAdapter
        (binding.rvFavourites.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.favouriteAdapter.favourite = { position, id, isFav ->
            viewModel.position = position
            viewModel.id = id
            viewModel.isFav = isFav
            viewModel.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }

        viewModel.favouriteAdapter.listener = { position, data ->
            viewModel.position = position
            val selectedCategory = viewModel.getCategoryType()

            val musicListData = MusicList.Data.Data(
                audio = data?.audio,
                affirmation = data?.affirmation,
                introAffirmation = data?.introAffirmation,
                customise = data?.customise,
                title = data?.title,
                description = data?.description,
                id = data?.id,
                views = data?.views,
                favouriteStatus = data?.favouriteStatus,
                thumbnail = data?.thumbnail,
                duration = data?.duration,
                audioName = data?.audioName,
                background = data?.background
            )


            val type: Int? =
                if (selectedCategory == AppConstant.HomeCategory.GUIDED_SLEEP && viewModel.categoryType?.value?.toInt() == 1) 1
                else viewModel.categoryType?.value?.toInt() ?: 0

            val musicDetail = getDataForPlayer(
                category = selectedCategory,
                data = musicListData,
                type = type
            )

            val categoryType = categoryType(
                categoryName = selectedCategory,
                type = viewModel.categoryType?.value?.toInt() ?: 0
            )

            val bundle = bundleOf(
                AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
                AppConstant.CATEGORY_NAME to categoryType?.name,
                AppConstant.REAL_CATEGORY to getRealCategoryType(
                    categoryName = selectedCategory,
                    type = viewModel.categoryType?.value?.toInt() ?: 0
                )?.name
            )


            /** If Music navigate to player screen instead to detail screen */
            val destination = when (categoryType) {
                AppConstant.HomeCategory.MUSIC -> PlayerActivity::class.java
                AppConstant.HomeCategory.TEXT_AFFIRMATION -> {
//                    if (data?.createdBy == 1) {
//                        bundle.putString(
//                            AppConstant.JOURNAL_DATA, Gson().toJson(
//                                JournalDetail(
//                                    date = data.createdAt ?: "",
//                                    description = data.description ?: "",
//                                    backgroundImage = data.background ?: "",
//                                    id = data.id.toString(),
//                                    categoryIcon = data.category?.get(0)?.cIcon,
//                                    categoryName = data.category?.get(0)?.cName,
//                                )
//                            )
//                        )
//                        FavouritesTextAffirmationActivity::class.java
//                    } else {
//                        bundle.putString(AppConstant.AFFIRMATION, data?.id.toString())
//                        TextAffirmationListActivity::class.java
//                    }
                    val image = if (data?.background == null) {
                        data?.thumbnail
                    } else {
                        data.background
                    }
                    bundle.putString(
                        AppConstant.JOURNAL_DATA, Gson().toJson(
                            JournalDetail(
                                date = data?.createdAt ?: "",
                                description = data?.description ?: "",
                                backgroundImage = image ?: "",
                                id = data?.id.toString(),
                                categoryIcon = data?.category?.get(0)?.cIcon,
                                categoryName = data?.category?.get(0)?.cName,
                            )
                        )
                    )
                    FavouritesTextAffirmationActivity::class.java
                }

                else -> AffirmationDetailActivity::class.java
            }

            activityResultForFav.launch(
                Intent(requireActivity(), destination).apply {
                    putExtras(bundle)
                }
            )
        }
    }

    private val activityResultForFav =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->
                    favouriteNotify(
                        fav = if (isFav) 1 else 0
                    )
                }
                result.data?.getBooleanExtra(AppConstant.MUSIC_CUSTOMIZE_DETAIL, false)
                    ?.let { isCustomize ->
                        if (isCustomize) {
                            getFavourites()
                        }
                    }
            }
        }

    private fun favouriteNotify(fav: Int) {
        viewModel.position?.let { row ->
            lifecycleScope.launch {
                if (fav == 1) {
                    val currentList = viewModel.favouriteAdapter.snapshot().items.toMutableList()
                    currentList[row].favouriteStatus = fav
                    viewModel.favouriteAdapter.submitData(PagingData.from(currentList.toImmutableList()))
                    viewModel.favouriteAdapter.notifyItemChanged(row)
                } else {
                    val currentList = viewModel.favouriteAdapter.snapshot().items.toMutableList()
                    currentList.removeAt(row)
                    viewModel.favouriteAdapter.submitData(PagingData.from(currentList.toImmutableList()))
                }
                val isListEmpty = viewModel.favouriteAdapter.snapshot().items.isEmpty()
                binding.tvNoDatFound.visibility(
                    isVisible = isListEmpty
                )
            }
        }
    }

}