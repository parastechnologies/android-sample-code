package com.in2bliss.ui.activity.home.fragment.explore

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.FragmentExploreBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.home.notification.NotificationActivity
import com.in2bliss.ui.activity.home.seeAll.SeeAllActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.getRealCategoryType
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : BaseFragment<FragmentExploreBinding>(
    layoutInflater = FragmentExploreBinding::inflate
) {
    private val viewModel: ExploreViewModel by viewModels()

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel
        setExploreData()
        setProfile()
        settingRecyclerView()
        onClick()
        observer()

        if (viewModel.musicListAdapter.currentList.isEmpty()) {
            viewModel.retryApiRequest(
                apiName = ApiConstant.EXPLORE
            )
        }
    }

    private fun setProfile() {
        binding.ivProfile.glide(
            requestManager = requestManager,
            image = BuildConfig.PROFILE_BASE_URL.plus(sharedPreference.userData?.data?.profilePicture),
            placeholder = R.drawable.ic_user_placholder,
            error = R.drawable.ic_user_placholder
        )
    }

    private fun setExploreData() {
        binding.rvRecyclerView.adapter = viewModel.exploreListAdapter
        binding.rvRecyclerView.itemAnimator = null
        viewModel.exploreListAdapter.submitList(viewModel.getAffirmationTypeList())
        viewModel.exploreListAdapter.onClick = { data ->
            viewModel.categoryType = data.affirmationType
            viewModel.retryApiRequest(
                apiName = ApiConstant.EXPLORE
            )
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exploreListResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = { response ->
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.musicListAdapter.submitList(mutableListOf())
                            viewModel.musicListAdapter.exploreCategoryType = viewModel.categoryType
                            if (response.data.isNullOrEmpty().not()) {
                                if (response.data?.get(0)?.data?.isEmpty() == true) {
                                    binding.tvNoDatFound.visible()
                                } else {
                                    binding.tvNoDatFound.gone()
                                }
                                viewModel.musicListAdapter.submitList(response.data)
                            } else {
                                binding.tvNoDatFound.visible()
                                viewModel.musicListAdapter.submitList(response.data)
                            }
                        }
                    },
                    errorBlock = {
                        viewModel.musicListAdapter.submitList(null)
                        binding.tvNoDatFound.visible()
                    },
                    showToast = false,
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )

                            }
                        )
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = { _ ->
                        viewModel.favouriteNotify(
                            if (viewModel.isFavourite == true) 0 else 1
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun onClick() {
        binding.ivNotification.setOnClickListener {
            requireActivity().intent(
                destination = NotificationActivity::class.java
            )
        }
        binding.clFavourites.setOnClickListener {
            seeAll(
                type = 0,
                title = getString(R.string.favourites)
            )
        }
        binding.clRecent.setOnClickListener {
            seeAll(
                type = 4,
                title = getString(R.string.recents)
            )
        }

        binding.edtSearch.doAfterTextChanged { text ->
            binding.ivCancelSearch.visibility((text?.length ?: "".length) > 0)
        }

        binding.edtSearch.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.EXPLORE
                )
            }
            p1 == EditorInfo.IME_ACTION_SEARCH
        }

        binding.ivCancelSearch.setOnClickListener {
            viewModel.search.set("")
            viewModel.retryApiRequest(
                apiName = ApiConstant.EXPLORE
            )
        }
    }

    private fun settingRecyclerView() {
        binding.rvMusicList.adapter = viewModel.musicListAdapter
        binding.rvMusicList.itemAnimator = null
        viewModel.musicListAdapter.isTitleImage = true

        viewModel.musicListAdapter.listener = { musicDetail, adapter, row, column, _ ->
            viewModel.nestedAdapter = adapter
            viewModel.rowPosition = row
            viewModel.columnPosition = column
            navigate(
                data = musicDetail
            )
        }
        viewModel.musicListAdapter.favourite = { adapter, isFav, id, row, column, _ ->
            viewModel.rowPosition = row
            viewModel.columnPosition = column
            viewModel.nestedAdapter = adapter
            viewModel.isFavourite = isFav
            viewModel.favMusicId = id
            viewModel.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }
        viewModel.musicListAdapter.seeAll = { title, id ->
            seeAll(
                type = id,
                title = title
            )
        }
    }

    private fun seeAll(
        type: Int,
        title: String,
    ) {


        Log.d("ascsacsac", "seeAll: ${viewModel.categoryType?.name}")
        val bundle = bundleOf(
            AppConstant.SCREEN_TITLE to title,
            AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
            AppConstant.TYPE to type,
            AppConstant.SCREEN_TYPE to AppConstant.SeeAllType.EXPLORE.name
        )


        activityResultForSeeAll.launch(
            Intent(requireActivity(), SeeAllActivity::class.java).apply {
                putExtras(bundle)
            }
        )
    }

    /**
     * Navigating to affirmation details screen
     * @param data
     * */
    private fun navigate(data: MusicList.Data.Data) {

        val categoryTypes: AppConstant.HomeCategory?
        var type: Int? = null
        when (viewModel.categoryType) {
            AppConstant.HomeCategory.SLEEP_AFFIRMATION -> {
                categoryTypes = AppConstant.HomeCategory.GUIDED_SLEEP
                type = 0
            }

            AppConstant.HomeCategory.SLEEP_MEDIATION -> {
                categoryTypes = AppConstant.HomeCategory.GUIDED_SLEEP
                type = 1
            }

            else -> {
                categoryTypes = viewModel.categoryType
            }
        }

        val musicDetail = getDataForPlayer(
            category = categoryTypes,
            data = data,
            type = type

        )
        val categoryType = categoryType(
            categoryName = categoryTypes,
            type = type
        )

        musicDetail.isCustomizationEnabled = data.customise!=null

        val bundle = bundleOf(
            AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
            AppConstant.CATEGORY_NAME to categoryType?.name,

            AppConstant.REAL_CATEGORY to getRealCategoryType(
                categoryName = viewModel.categoryType,
                type = type
            )?.name
        )
        Log.d("Ascsacsacsa", "navigate: ${categoryType?.name}")

        activityResultForFav.launch(
            Intent(requireContext(), AffirmationDetailActivity::class.java).apply {
                putExtras(bundle)
            }
        )
    }

    private val activityResultForFav =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->
                    viewModel.favouriteNotify(
                        fav = if (isFav) 1 else 0
                    )
                }
                result.data?.getBooleanExtra(AppConstant.MUSIC_CUSTOMIZE_DETAIL, false)
                    ?.let { isCustomize ->
                        if (isCustomize) {
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.EXPLORE
                            )
                        }
                    }
            }
        }

    private val activityResultForSeeAll =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->
                    if (isFav) {
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.EXPLORE
                        )
                    }
                }
            }
        }
}
