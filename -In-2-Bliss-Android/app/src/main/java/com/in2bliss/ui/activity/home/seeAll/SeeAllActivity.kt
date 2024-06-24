package com.in2bliss.ui.activity.home.seeAll

import android.app.Activity
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivitySeeAllBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.addAffirmation.AddAffirmationActivity
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.getRealCategoryType
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

@AndroidEntryPoint
class SeeAllActivity : BaseActivity<ActivitySeeAllBinding>(
    layout = R.layout.activity_see_all
) {

    private val viewModel: SeeAllViewModel by viewModels()

    override fun init() {
        binding.data = viewModel
        backPressed()
        getIntentData()
        setRecyclerview()
        onClick()
        observer()
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            popUpWithResult()
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popUpWithResult()
            }
        })
    }

    private fun popUpWithResult() {
        val intent = Intent()
        intent.putExtra(AppConstant.FAVOURITE, viewModel.isFavourite)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setRecyclerview() {
        binding.rvRecyclerView.adapter = viewModel.adapter
        (binding.rvRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.adapter.listener = { data, _ ->
//            viewModel.position = position
            navigate(
                data = data
            )
        }

        viewModel.adapter.favourite = { position, id, isFav ->
            viewModel.position = position
            viewModel.favMusicId = id
            viewModel.musicFavourite = isFav
            viewModel.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }

        viewModel.adapter.editListener = { data ->

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
                screenType = AppConstant.CreatedAffirmationEdit.SEE_ALL_AFFIRMATION.name,
                screenName = binding.toolBar.tvTitle.text.toString()
            )

            val bundle = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(createAffirmationData),
                AppConstant.CATEGORY_NAME to viewModel.categoryName?.name
            )

            intent(
                destination = AddAffirmationActivity::class.java,
                bundle = bundle
            )
        }
    }

    /**
     * Navigating to affirmation details screen
     * @param data
     * */
    private fun navigate(data: SeeAllResponse.Data) {


        val categoryTypes: AppConstant.HomeCategory?
        when (viewModel.categoryName) {
            AppConstant.HomeCategory.SLEEP_AFFIRMATION -> {
                categoryTypes = AppConstant.HomeCategory.GUIDED_SLEEP
                viewModel.type = 0
            }

            AppConstant.HomeCategory.SLEEP_MEDIATION -> {
                categoryTypes = AppConstant.HomeCategory.GUIDED_SLEEP
                viewModel.type = 1
            }

//            AppConstant.HomeCategory.GUIDED_AFFIRMATION -> {
//                categoryTypes = AppConstant.HomeCategory.GUIDED_AFFIRMATION
//
//            }

            AppConstant.HomeCategory.GUIDED_SLEEP -> {
                categoryTypes = viewModel.categoryName

//                    if (viewModel.type == 2) {
//                    AppConstant.HomeCategory.MUSIC
//                } else {
//                    viewModel.categoryName
//                }
            }

            else -> {
                categoryTypes = viewModel.categoryName
            }
        }
        val musicListData = MusicList.Data.Data(
            audio = data.audio,
            affirmation = data.affirmation,
            introAffirmation = data.introAffirmation,
            customise = data.customise,
            title = data.title,
            description = data.description,
            id = data.id,
            views = data.views,
            favouriteStatus = data.favouriteStatus,
            thumbnail = data.thumbnail,
            duration = data.duration,
            audioName = data.audioName
        )

        val musicDetail = getDataForPlayer(
            category = categoryTypes,
            data = musicListData,
            type = viewModel.type
        )
        val categoryType = categoryType(
            categoryName = categoryTypes,
            type = viewModel.type
        )

        val bundle = bundleOf(
            AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
            AppConstant.CATEGORY_NAME to categoryType?.name,
            AppConstant.REAL_CATEGORY to getRealCategoryType(
                categoryName = categoryTypes,
                type = viewModel.type
            )?.name
        )


        val destination = if (categoryType == AppConstant.HomeCategory.MUSIC) {
            PlayerActivity::class.java
        } else AffirmationDetailActivity::class.java


        activityResultForFav.launch(
            Intent(this, destination).apply {
                putExtras(bundle)
            }
        )

//
//        activityResultForFav.launch(
//            Intent(this, AffirmationDetailActivity::class.java).apply {
//                putExtras(bundle)
//            }
//        )
    }

    private val activityResultForFav =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->

                    /** Using this to hit api again in previous screen for fav update*/
                    viewModel.isFavourite = true

                    /** Removing affirmation if unFavourite in favourite */
                    if (viewModel.type == 0 && isFav.not()) {
                        removeAffirmation()
                        return@registerForActivityResult
                    }
                    viewModel.favouriteNotify(
                        fav = if (isFav) 1 else 0
                    )
                }
                result.data?.getBooleanExtra(AppConstant.MUSIC_CUSTOMIZE_DETAIL, false)
                    ?.let { isCustomize ->
                        if (isCustomize) viewModel.gettingMusicList()
                    }
            }
        }

    /**
     * get intent data [getIntentData]
     * */

    private fun getIntentData() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryName = when (categoryName) {
                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                AppConstant.HomeCategory.GUIDED_SLEEP.name -> AppConstant.HomeCategory.GUIDED_SLEEP
                AppConstant.HomeCategory.MUSIC.name -> AppConstant.HomeCategory.MUSIC
                AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> AppConstant.HomeCategory.WISDOM_INSPIRATION
                AppConstant.HomeCategory.SLEEP_AFFIRMATION.name -> AppConstant.HomeCategory.SLEEP_AFFIRMATION
                AppConstant.HomeCategory.SLEEP_MEDIATION.name -> AppConstant.HomeCategory.SLEEP_MEDIATION
                else -> null
            }
        }

        /** Category type for explore */
        viewModel.categoryType = when (viewModel.categoryName) {
            AppConstant.HomeCategory.GUIDED_MEDITATION -> ApiConstant.ExploreType.MEDITATION.value
            AppConstant.HomeCategory.GUIDED_AFFIRMATION -> ApiConstant.ExploreType.AFFIRMATION.value
            AppConstant.HomeCategory.WISDOM_INSPIRATION -> ApiConstant.ExploreType.WISDOM.value
            AppConstant.HomeCategory.MUSIC -> ApiConstant.ExploreType.MUSIC.value
            AppConstant.HomeCategory.QUOTES -> ApiConstant.ExploreType.QUOTE.value
            AppConstant.HomeCategory.SLEEP_AFFIRMATION -> ApiConstant.ExploreType.SLEEP_AFFIRMATION.value
            AppConstant.HomeCategory.SLEEP_MEDIATION -> ApiConstant.ExploreType.SLEEP_MEDITATION.value
            else -> null
        }


        intent.getStringExtra(AppConstant.IS_GENERAL).let {
            viewModel.isGeneral = it
        }

        intent.getStringExtra(AppConstant.SCREEN_TYPE).let { screenType ->
            viewModel.seeAllType = when (screenType) {
                AppConstant.SeeAllType.EXPLORE.name -> AppConstant.SeeAllType.EXPLORE
                AppConstant.SeeAllType.SLEEP.name -> AppConstant.SeeAllType.SLEEP
                else -> null
            }
        }

        intent.getIntExtra(AppConstant.TYPE, 0).let { type ->
            viewModel.type = type
        }

        intent.getStringExtra(AppConstant.SCREEN_TITLE)?.let { title ->
            binding.toolBar.tvTitle.text = title
        }

        intent.getStringExtra(AppConstant.CATEGORY_ID)?.let { categoryId ->
            viewModel.categoryId = categoryId
        }

        intent.getStringExtra(AppConstant.SUB_CATEGORY_ID)?.let { subCategoryId ->
            viewModel.subCategoryId = subCategoryId
        }
    }

    private fun observer() {
        viewModel.adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbPopular.gone()
                    binding.pbSeeAllNewData.gone()
                }

                is LoadState.Loading -> {
                    val isEmptyList = viewModel.adapter.snapshot().isEmpty()
                    binding.pbPopular.visibility(isVisible = isEmptyList)
                    binding.pbSeeAllNewData.visibility(isVisible = isEmptyList.not())
                }

                is LoadState.NotLoading -> {
                    val isListEmpty = viewModel.adapter.snapshot().items.isEmpty()
                    binding.tvNoDatFound.visibility(
                        isVisible = isListEmpty
                    )
                    binding.pbPopular.gone()
                    binding.pbSeeAllNewData.gone()
                }
            }
        }

        viewModel.gettingMusicList()

        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@SeeAllActivity,
                    success = { _ ->

                        /** Using this to hit api again in previous screen for fav update*/
                        viewModel.isFavourite = true

                        /** Removing affirmation if unFavourite in favourite */
                        if (viewModel.type == 0 && viewModel.musicFavourite &&
                            viewModel.categoryName != AppConstant.HomeCategory.GUIDED_SLEEP
                        ) {
                            removeAffirmation()
                            return@handleResponse
                        }
                        viewModel.favouriteNotify(
                            fav = if (viewModel.musicFavourite) 0 else 1
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

    private fun removeAffirmation() {
        viewModel.position?.let { row ->
            lifecycleScope.launch {
                val currentList = viewModel.adapter.snapshot().items.toMutableList()
                currentList.removeAt(row)
                viewModel.adapter.submitData(PagingData.from(currentList.toImmutableList()))
                binding.tvNoDatFound.visibility(isVisible = currentList.isEmpty())
            }
        }
    }
}