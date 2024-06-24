package com.in2bliss.ui.activity.home.affirmationExplore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityAffirmationExploreBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.addAffirmation.AddAffirmationActivity
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet.MusicCategoryBottomSheet
import com.in2bliss.ui.activity.home.affirmationExplore.durationSearch.DurationSearchBottomSheet
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.ui.activity.home.seeAll.SeeAllActivity
import com.in2bliss.ui.activity.streakCompleteDialog
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.getRealCategoryType
import com.in2bliss.utils.extension.hideKeyboard
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AffirmationExploreActivity : BaseActivity<ActivityAffirmationExploreBinding>(
    layout = R.layout.activity_affirmation_explore
) {

    @Inject
    lateinit var sharedPreferences: SharedPreference
    private val viewModel: AffirmationExploreViewModel by viewModels()
    private var durationSearchBottomSheet: DurationSearchBottomSheet? = null

    override fun init() {
        binding.data = viewModel
        getIntentData()
        settingRecyclerView()
        onClick()
        observer()
//        if (viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION) {
//            streakCompleteDialog(
//                streakCount = 7,
//                activity = this
//            )
//        }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.musicListResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationExploreActivity,
                    success = { response ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION) {
                                if (it.data?.streak == 7 && sharedPreferences.isShowed7Streak()
                                        .not()
                                ) {
                                    streakCompleteDialog(7, this@AffirmationExploreActivity)
                                    sharedPreferences.showed7Streak()
                                }
                            }

                            viewModel.filterStart = null
                            viewModel.filterEnd = null

                            val userData = sharedPreferences.userData
                            userData?.data?.sleepStatus = 1
                            sharedPreferences.userData = userData

                            awaitAll(
                                async {
                                    viewModel.recentSearchAdapter.submitList(response.searchHistory)
                                },
                                async {
                                    viewModel.musicListAdapter.submitList(response.data) {
                                        binding.tvNoDatFound.visibility(
                                            isVisible = viewModel.musicListAdapter.currentList.isEmpty()
                                        )
                                    }
                                }
                            )
                        }
                    },
                    errorBlock = {
                        viewModel.filterStart = null
                        viewModel.filterEnd = null
                        viewModel.musicListAdapter.submitList(null)
                        binding.tvNoDatFound.visible()
                    },
                    showToast = false,
                    error = { message, apiName ->
                        alertDialogBox(
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

        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationExploreActivity,
                    success = { _ ->
                        viewModel.favouriteNotify(
                            if (viewModel.isFavourite == true) 0 else 1
                        )
                    },
                    error = { message, apiName ->
                        alertDialogBox(
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

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivFilter.setOnClickListener {
            when (viewModel.categoryType) {
                AppConstant.HomeCategory.GUIDED_MEDITATION -> {
                    if (durationSearchBottomSheet?.dialog?.isShowing == true) {
                        return@setOnClickListener
                    }
                    durationSearchBottomSheet = DurationSearchBottomSheet().apply {
                        show(
                            supportFragmentManager, null
                        )
                        selectedDuration = { start, end ->
                            viewModel.filterStart = start
                            viewModel.filterEnd = end
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.GUIDED_MEDIATION
                            )
                        }
                    }
                }

                else -> {
                    MusicCategoryBottomSheet().apply {
                        arguments = bundleOf(
                            AppConstant.IS_MUSIC_CATEGORY to false
                        )
                        show(
                            supportFragmentManager, null
                        )
                    }
                }
            }
        }

        binding.btnCreateAffirmation.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.CATEGORY_NAME to viewModel.categoryType?.name
            )
            intent(
                destination = AddAffirmationActivity::class.java,
                bundle = bundle
            )
        }

        binding.edtSearch.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.apiRequest()
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_SEARCH
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

        binding.ivCancelSearch.setOnClickListener {
            viewModel.search.set("")
            viewModel.apiRequest()
        }
    }

    private fun getIntentData() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryType ->
            viewModel.categoryType = when (categoryType) {
                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
                AppConstant.HomeCategory.GUIDED_SLEEP.name -> AppConstant.HomeCategory.GUIDED_SLEEP
                AppConstant.HomeCategory.MUSIC.name -> AppConstant.HomeCategory.MUSIC
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> AppConstant.HomeCategory.WISDOM_INSPIRATION
                else -> null
            }
        }

        if (viewModel.categoryType == AppConstant.HomeCategory.GUIDED_SLEEP) {
            binding.edtSearch.setHint(R.string.search_sleep_track)
        }
        intent.getStringExtra(AppConstant.IS_GENERAL)?.let { isGeneral ->
            viewModel.isGeneral.set(isGeneral)
        }

        intent.getStringExtra(AppConstant.CATEGORY_ID)?.let { categoryId ->
            viewModel.categoryId = categoryId
        }
        intent.getBooleanExtra(AppConstant.IS_GUIDED, false).let { isGuided ->
            viewModel.isGuided = isGuided
        }

        intent.getStringExtra(AppConstant.TYPE)?.let { type ->
            viewModel.type = type
        }
        intent.getStringExtra(AppConstant.SUB_CATEGORY_ID)?.let { subCategoryId ->
            viewModel.subCategoryId = subCategoryId
        }

        val title = when (viewModel.categoryType) {
            AppConstant.HomeCategory.GUIDED_MEDITATION -> getString(R.string.guided_meditation)
            AppConstant.HomeCategory.GUIDED_SLEEP -> getString(R.string.guided_sleep)
            AppConstant.HomeCategory.MUSIC -> getString(R.string.music)
            AppConstant.HomeCategory.CREATE_AFFIRMATION -> getString(R.string.my_created_affirmations)
            AppConstant.HomeCategory.GUIDED_AFFIRMATION -> getString(R.string.guided_affirmation)
            AppConstant.HomeCategory.WISDOM_INSPIRATION -> getString(R.string.wisdom_inspiration)
            else -> ""
        }

        binding.tvTitle.text = title

        if (viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION) {
            binding.edtSearch.setHint(R.string.search_created_affirmations)
        }

//        binding.cvStreak.visibility(
//            isVisible = viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION
//        )

        binding.btnCreateAffirmation.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION
        )

        binding.ivFilter.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION
        )

        binding.viewAlign.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.GUIDED_MEDITATION
        )

        binding.clRecent.visibility(
            isVisible = (viewModel.categoryType != AppConstant.HomeCategory.CREATE_AFFIRMATION &&
                    viewModel.categoryType != AppConstant.HomeCategory.GUIDED_SLEEP)
        )

        binding.clFavourites.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.GUIDED_SLEEP
        )

        binding.ivAlign.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.GUIDED_SLEEP
        )

        /** Navigating to see all activity if data is not null */
        intent.getStringExtra(AppConstant.SCREEN_TITLE)?.let { seeAllTitle ->
            intent?.getIntExtra(AppConstant.TYPE, -1)?.let { type ->
                seeAll(
                    type = type,
                    title = seeAllTitle
                )
            }
        }

        viewModel.apiRequest()
    }

    private fun settingRecyclerView() {

        binding.rvMusicList.adapter = viewModel.musicListAdapter
        binding.rvSearchKeys.adapter = viewModel.recentSearchAdapter
        binding.rvMusicList.itemAnimator = null
        binding.rvSearchKeys.itemAnimator = null

        viewModel.recentSearchAdapter.listener = { search ->
            viewModel.search.set(search)
            viewModel.apiRequest()
        }

        viewModel.musicListAdapter.listener = { musicDetail, adapter, row, column, type ->
            viewModel.typeForGuidedSleep = type
            viewModel.nestedAdapter = adapter
            viewModel.rowPosition = row
            viewModel.columnPosition = column
            navigate(
                data = musicDetail
            )
        }

        viewModel.musicListAdapter.favourite = { adapter, isFav, id, row, column, type ->
            viewModel.typeForGuidedSleep = type
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
                title = title,
            )
        }

        viewModel.musicListAdapter.editListener = { row, column, data, type ->
            viewModel.columnPosition = column
            viewModel.rowPosition = row

            val createAffirmationData = CreateAffirmation(
                affirmationTitle = data.title,
                affirmationDetail = data.description,
                isEdit = true,
                affirmationId = data.id,
                audioFileStringUri = data.audio,
                transcript = data.transcript,
                affirmationBackground = data.thumbnail,
                audioDuration = data.duration?.toLong(),
                audioType = if (data.audioType == 1) ApiConstant.AffirmationAudioType.UPLOAD else ApiConstant.AffirmationAudioType.RECORDED,
                screenType = AppConstant.CreatedAffirmationEdit.CREATE_AFFIRMATION.name,
                type = type
            )

            val bundle = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(createAffirmationData),
                AppConstant.CATEGORY_NAME to viewModel.categoryType?.name
            )

            intent(
                destination = AddAffirmationActivity::class.java,
                bundle = bundle
            )
        }
    }

    private fun seeAll(
        type: Int,
        title: String
    ) {


        val bundle = Bundle()
        bundle.putString(AppConstant.SCREEN_TITLE, title)
        bundle.putString(AppConstant.CATEGORY_ID, viewModel.categoryId)
        bundle.putString(AppConstant.SUB_CATEGORY_ID, viewModel.subCategoryId)
        bundle.putString(AppConstant.CATEGORY_NAME, viewModel.categoryType?.name)
        bundle.putString(AppConstant.IS_GENERAL, viewModel.isGeneral.get().toString())
        bundle.putInt(AppConstant.TYPE, type)
        if (viewModel.categoryType == AppConstant.HomeCategory.GUIDED_SLEEP) {
            bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.SeeAllType.SLEEP.name)
        }


        activityResultForSeeAll.launch(
            Intent(this, SeeAllActivity::class.java).apply {
                putExtras(bundle)
            }
        )
    }

    /**
     * Navigating to affirmation details screen
     * @param data
     * */
    private fun navigate(data: MusicList.Data.Data) {

        Log.d("ascsacsacsa", "navigate: ${viewModel.categoryType}")
        Log.d("ascsacsacsa", "navigate: ${viewModel.typeForGuidedSleep}")
        val category = viewModel.categoryType
        val musicDetail = getDataForPlayer(
            category = category,
            type = viewModel.typeForGuidedSleep,
            data = data
        )
        val categoryType = categoryType(
            categoryName = viewModel.categoryType,
            type = viewModel.typeForGuidedSleep
        )

        musicDetail.isCustomizationEnabled = data.customise != null

        val bundle = bundleOf(
            AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
            AppConstant.CATEGORY_NAME to categoryType?.name,
            AppConstant.REAL_CATEGORY to getRealCategoryType(
                categoryName = viewModel.categoryType,
                type = viewModel.typeForGuidedSleep
            )?.name


        )

        /** If Music navigate to player screen instead to detail screen */
        val destination = if (categoryType == AppConstant.HomeCategory.MUSIC) {
            PlayerActivity::class.java
        } else AffirmationDetailActivity::class.java

        activityResultForFav.launch(
            Intent(this, destination).apply {
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
                            viewModel.apiRequest()
                        }
                    }
            }
        }

    private val activityResultForSeeAll =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->
                    if (isFav) {
                        viewModel.apiRequest()
                    }
                }
            }
        }
}
