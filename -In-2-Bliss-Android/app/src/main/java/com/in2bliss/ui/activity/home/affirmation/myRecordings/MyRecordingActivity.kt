package com.in2bliss.ui.activity.home.affirmation.myRecordings

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityMyRecordingBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.ChooseBackgroundActivity
import com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet.MusicCategoryBottomSheet
import com.in2bliss.ui.activity.home.player.PlayerViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.getCurrentTime
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyRecordingActivity : BaseActivity<ActivityMyRecordingBinding>(
    layout = R.layout.activity_my_recording
) {

    @Inject
    lateinit var requestManager: RequestManager
    private val musicViewModel: PlayerViewModel by viewModels()
    private val viewModel: MyRecordingsViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.my_recording)
        binding.data = viewModel
        gettingBundleData()
        onClick()
        observer()
        isBackgroundMusicSelected()
    }


    private fun observer() {
        /** Play or pause button drawable change */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.player?.musicPLayingState()?.collectLatest { isPlaying ->
                binding.ivPlayAndPause.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MyRecordingActivity,
                        if (isPlaying) R.drawable.ic_music_pause else R.drawable.ic_music_play
                    )
                )
            }
        }

        /** Seek bar progress */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.player?.musicProgress()?.collectLatest { mediaProgress ->
                viewModel.startTime.set(mediaProgress.musicProgress)
                binding.musicPlayerSlider.progress = mediaProgress.seekBarProgress.toInt()
            }
        }

        /** Music end time */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.player?.musicEndTime()?.collectLatest { mediaProgress ->
                viewModel.endTime.set(mediaProgress.musicProgress)
                binding.musicPlayerSlider.max = mediaProgress.seekBarProgress.toInt()
                viewModel.createAffirmation?.audioDuration = if (
                    mediaProgress.seekBarProgress != 0L
                ) mediaProgress.seekBarProgress / 1000 else 0L

            }
        }

        lifecycleScope.launch {
            viewModel.player?.musicBuffering()?.collectLatest { isVisible ->
                binding.buffering.visibility(
                    isVisible = isVisible
                )
            }
        }

        lifecycleScope.launch {
            musicViewModel.musicCategories.collectLatest {
                handleResponse(
                    response = it,
                    context = this@MyRecordingActivity,
                    showToast = false,
                    success = { response ->
                        musicViewModel.musicCategoriesData = response
                        synchronized(this) {
                            musicCategoriesBottomSheet()
                        }
                    },
                    errorBlock = {
                        musicViewModel.musicCategoriesData = null
                        synchronized(this) {
                            musicCategoriesBottomSheet()
                        }
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

        lifecycleScope.launch {
            musicViewModel.musicList.collectLatest {
                handleResponse(
                    response = it,
                    context = this@MyRecordingActivity,
                    success = { response ->
                        synchronized(this) {
                            musicListBottomSheet(response)
                        }
                    },
                    showToast = false,
                    errorBlock = {
                        synchronized(this) {
                            musicListBottomSheet(null)
                        }
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

    /**
     * get the bundle data from bundle [gettingBundleData]
     * */
    private fun gettingBundleData() {
        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { createAffirmation ->
            viewModel.createAffirmation =
                Gson().fromJson(createAffirmation, CreateAffirmation::class.java)
        }

        if (viewModel.createAffirmation != null) {
            val data = viewModel.createAffirmation

            viewModel.initializePLayer(
                context = this
            )

            viewModel.player?.addBackgroundMedia(
                mediaUri = data?.audioFileStringUri.orEmpty().toUri(),
                playWhenReady = false
            )
            viewModel.player?.changeRepeatMode(true)
            viewModel.title.set(data?.affirmationTitle)
            viewModel.dateAndTime.set(
                formatDate(
                    date = "${getCurrentDate()} ${getCurrentTime()}",
                    inputFormat = "yyyy-MM-dd HH:mm",
                    outPutFormat = "EEEE, dd MMM hh:mm:aa"
                )
            )
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(viewModel.createAffirmation),
                AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.CREATE_AFFIRMATION.name
            )
            intent(
                destination = ChooseBackgroundActivity::class.java,
                bundle = bundle
            )
        }

        binding.ivPlayAndPause.setOnClickListener {
            viewModel.player?.playOrPausePlayer(
                isPlaying = viewModel.player?.musicPLayingState()?.value?.not() ?: false
            )
        }
        binding.ivForward10.setOnClickListener {
            val progress = binding.musicPlayerSlider.progress
            viewModel.player?.changePlayerProgress(
                progress = progress.toLong() + 10000L
            )
        }
        binding.ivBack10.setOnClickListener {
            val progress = binding.musicPlayerSlider.progress
            viewModel.player?.changePlayerProgress(
                progress = progress.toLong() - 10000L
            )
        }
        binding.musicPlayerSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.player?.changePlayerProgress(
                        progress = progress.toLong()
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.clMusicDuringAffirmation.setOnClickListener {
            if (musicViewModel.musicCategoriesData == null) {
                musicViewModel.retryApiRequest(
                    apiName = ApiConstant.MUSIC_CATEGORIES
                )
                return@setOnClickListener
            }
            musicCategoriesBottomSheet()
        }
    }

    /**
     * Music list bottom sheet and setting the selected background music and its details
     * */
    private fun musicListBottomSheet(data: MusicList?) {
        MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_LIST_DATA to if (data == null) null else Gson().toJson(data),
                AppConstant.IS_MUSIC_CATEGORY to false
            )

            backgroundMusicUrl = { selectedBackgroundMusic ->

                viewModel.player?.playOrPausePlayer(
                    isPlaying = true
                )

                viewModel.createAffirmation?.backgroundMusic = selectedBackgroundMusic?.audio
                viewModel.createAffirmation?.audioName = selectedBackgroundMusic?.audioName

                isBackgroundMusicSelected(
                    isSelected = true
                )
                settingBackgroundMusicData(
                    title = selectedBackgroundMusic?.audioName,
                    image = selectedBackgroundMusic?.thumbnail
                )
                viewModel.musicCategoriesBottomSheet?.dismiss()
            }
        }.also {
            it.show(
                supportFragmentManager, null
            )
        }
    }

    private fun settingBackgroundMusicData(
        title: String?,
        image: String?
    ) {
        binding.tvBackgroundMusicTitle.text = title
        binding.ivBackgroundImage.glide(
            requestManager = requestManager,
            image = BuildConfig.MUSIC_BASE_URL.plus(image.orEmpty()),
            placeholder = R.drawable.ic_error_place_holder,
            error = R.drawable.ic_error_place_holder
        )
    }

    private fun isBackgroundMusicSelected(
        isSelected: Boolean = false
    ) {
        binding.tvNoBackgroundMusicSelected.visibility(
            isVisible = isSelected.not()
        )
        binding.backgroundMusic.visibility(
            isVisible = isSelected
        )
    }

    private fun musicCategoriesBottomSheet() {

        viewModel.player?.playOrPausePlayer(
            isPlaying = false
        )

        if (viewModel.musicCategoriesBottomSheet != null) viewModel.musicCategoriesBottomSheet?.dismiss()

        viewModel.musicCategoriesBottomSheet = MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_CATEGORIES_DATA to Gson().toJson(musicViewModel.musicCategoriesData),
                AppConstant.IS_MUSIC_CATEGORY to true
            )

            categoryId = { categoryId ->
                musicViewModel.categoryId = categoryId
                musicViewModel.retryApiRequest(
                    apiName = ApiConstant.MUSIC_LIST
                )
            }
        }
        viewModel.musicCategoriesBottomSheet?.closed = {
            viewModel.player?.playOrPausePlayer(
                isPlaying = true
            )
        }
        viewModel.musicCategoriesBottomSheet?.show(
            supportFragmentManager, null
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.player?.playOrPausePlayer(
            isPlaying = false
        )
    }
}