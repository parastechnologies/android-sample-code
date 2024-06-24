package com.in2bliss.ui.activity.home.music

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityMusicBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.ui.activity.home.player.PlayerViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicActivity : BaseActivity<ActivityMusicBinding>(R.layout.activity_music) {

    val viewModel: PlayerViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.text = getString(R.string.music)
        viewModel.categoryName = AppConstant.HomeCategory.MUSIC
        viewModel.retryApiRequest(
            apiName = ApiConstant.MUSIC_CATEGORIES
        )
        settingRecyclerData()
        onClick()
        observe()
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.musicCategories.collectLatest {
                handleResponse(response = it,
                    context = this@MusicActivity,
                    showToast = false,
                    success = { response ->
                        viewModel.categoryId = response.data?.get(0)?.id
                        viewModel.retryApiRequest(ApiConstant.MUSIC_LIST)
                        viewModel.musicCategoriesAdapter.submitList(response.data)
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }

        lifecycleScope.launch {
            viewModel.musicList.collectLatest {
                handleResponse(
                    response = it,
                    context = this@MusicActivity,
                    success = { response ->
                        viewModel.musicAdapter.submitList(response.data) {
                            binding.tvNoAffirmationAdded.visibility(
                                isVisible = viewModel.musicAdapter.currentList.isEmpty()
                            )
                        }
                    }, showToast = false,
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }

        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@MusicActivity,
                    success = { _ ->
                        favouriteNotify(
                            isFav = if (viewModel.isFavourite == true) 0 else 1
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

    private fun settingRecyclerData() {
        /** Category list adapter */
        binding.rvMusicCategoryRecycler.adapter = viewModel.musicCategoriesAdapter
        (binding.rvMusicCategoryRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        viewModel.musicCategoriesAdapter.onClick = { id ->
            viewModel.categoryId = id
            viewModel.retryApiRequest(ApiConstant.MUSIC_LIST)
        }

        /** Music list adapter */
        binding.rvMusicList.adapter = viewModel.musicAdapter
        (binding.rvMusicList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false

        viewModel.musicAdapter.onClick = { musicData ->

            val musicListData = com.in2bliss.data.model.musicList.MusicList.Data.Data(
                audio = musicData.audio,
                audioName = musicData.audioName,
                thumbnail = musicData.thumbnail,
                favouriteStatus = musicData.favouriteStatus,
                id = musicData.id
            )

            val musicDetails = getDataForPlayer(
                category = AppConstant.HomeCategory.MUSIC,
                data = musicListData
            )

            val bundle = bundleOf(
                AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.MUSIC.name,
                AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetails)
            )
            activityResultForFav.launch(
                Intent(this@MusicActivity, PlayerActivity::class.java).apply {
                    putExtras(bundle)
                }
            )
        }

        viewModel.musicAdapter.favourite = { position, id, isFav ->
            viewModel.position = position
            viewModel.isFavourite = isFav
            viewModel.favMusicId = id
            viewModel.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }
    }

    private val activityResultForFav =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->
                    favouriteNotify(
                        isFav = if (isFav) 1 else 0
                    )
                }
            }
        }

    private fun favouriteNotify(
        isFav: Int
    ) {
        viewModel.position?.let { row ->
            val currentList = viewModel.musicAdapter.currentList.toMutableList()
            currentList[row].favouriteStatus = isFav
            viewModel.musicAdapter.submitList(currentList)
            viewModel.musicAdapter.notifyItemChanged(row)
        }
    }
}

