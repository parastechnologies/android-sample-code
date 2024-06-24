package com.in2bliss.ui.activity.home.player

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.databinding.Observable
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityPlayerBinding
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.domain.RoomDataBaseInterface
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailViewModel
import com.in2bliss.ui.activity.home.affirmationDetails.customizeAffirmationBottomSheet.CustomiseAffirmationFragment
import com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet.MusicCategoryBottomSheet
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.downloadWithWorkManager
import com.in2bliss.utils.extension.formatSecondsToHhMm
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.gettingDownloadStatus
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.inVisible
import com.in2bliss.utils.extension.loadSvg
import com.in2bliss.utils.extension.showSnackBar
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.showToastLong
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class PlayerActivity : BaseActivity<ActivityPlayerBinding>(
    layout = R.layout.activity_player
) {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var imageRequest: ImageRequest.Builder

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var roomDataBaseInterface: RoomDataBaseInterface

    @Inject
    lateinit var downloadStatusListener: DownloadStatusListenerInterface

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val viewModel: PlayerViewModel by viewModels()

    private var musicCategoriesBottomSheet: MusicCategoryBottomSheet? = null
    private var musicListBottomSheet: MusicCategoryBottomSheet? = null
    private var customiseAffirmationFragment: CustomiseAffirmationFragment? = null

    private val viewModelAffirmationDetail: AffirmationDetailViewModel by viewModels()
    private var selectBackgroundMusic: String? = null
    private var selectBackgroundImage: String? = null
    private var selectBackgroundTitle: String? = null
    private var isBackgroundSelect: Boolean? = false

    private var seekWithMusic: Long? = null
    private var seekWithSeekWithAffirmation: Long? = null
    override fun init() {
        binding.data = viewModel
        binding.tvMusicTitle.ellipsize = TextUtils.TruncateAt.END
        gettingBundle()
        checkIsDownloadScreenOrNot()
        backPressed()
        onClick()
        gettingRoomDataBase()

        /**
         * Changing view visibility if customization is enabled
         * */
        if (viewModel.musicDetails?.isCustomizationEnabled == true &&
            viewModel.isAffirmationIntroduction.not()
        ) {
            affirmationCustomTime(
                isSettingAudioInPlayers = false
            )
        }

        /** Setting customization background music url if enabled */
        if (viewModel.musicDetails?.isCustomizationEnabled == true &&
            viewModel.musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == true &&
            viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicUrl.isNullOrEmpty().not()
        ) {
            /**
             * Not Setting background music  setNewBackgroundMusic param to false because music url already set
             * when initializing player
             * */
            settingSelectedBackgroundMusic(
                title = viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicTitle.orEmpty(),
                image = viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicImage.orEmpty(),
                musicUrl = viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicUrl.orEmpty(),
                setNewBackgroundMusic = false
            )
        }
        viewModel.initializingPlayers(context = this)
        observer()
        binding.ivFav.gone()
    }

    private fun checkIsDownloadScreenOrNot() {
        if (viewModel.categoryName?.name == AppConstant.HomeCategory.DOWNLOAD.name) {
            binding.ivShare.gone()
            binding.ivDownload.gone()
            binding.ivFav.gone()
            if (viewModel.musicDetails?.downloadCategoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                viewModel.musicDetails?.downloadCategoryName == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                viewModel.musicDetails?.downloadCategoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION ||
                viewModel.musicDetails?.downloadCategoryName == AppConstant.HomeCategory.WISDOM_INSPIRATION
            ) {
                binding.secondaryVolume.visible()
            }
            if (viewModel.musicDetails?.musicBackground?.takeLast(3) == "svg") {
                binding.ivAffirmationProfileBg.loadSvg(
                    imageLoader = imageLoader,
                    imageRequest = imageRequest,
                    url = viewModel.musicDetails?.musicThumbnail.toString(),
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            } else {
                binding.ivAffirmationProfileBg.glide(
                    requestManager = requestManager,
                    image = viewModel.musicDetails?.musicThumbnail.toString(),
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }

        }
    }

    private fun gettingRoomDataBase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val downloadedMusicList = roomDataBaseInterface.getList()
            downloadedMusicList.forEach { downloadedMusic ->
                if (downloadedMusic.musicUrl == viewModel.musicDetails?.musicUrl &&
                    downloadedMusic.id == viewModel.musicDetails?.musicId &&
                    downloadedMusic.userId == (sharedPreference.userData?.data?.id ?: 0)
                ) {
                    viewModel.changeDownloadStatus(
                        downloadStatus = AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
                    )
                    return@launch
                }
            }
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    endSession()
                }
            })
    }

    private fun popUpWithFavResult() {
        val intent = Intent()
        intent.putExtra(
            AppConstant.FAVOURITE,
            viewModel.musicDetails?.musicFavouriteStatus == 1
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun observer() {


        viewModel.customBackTime.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (viewModel.customBackTime.get() == "00:00:10") {
                    val vol = (viewModel.backgroundMusicPlayer?.getPlayerVolume() ?: 0f) * 100
                    val minus = vol / 15
                    fadeSoundSlowly(minus, null)
                }
            }
        })

        viewModel.customTime.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (viewModel.customTime.get() == "00:00:10") {
                    val vol2 = (viewModel.mediaController?.getPlayerVolume() ?: 0f) * 100
                    val minusMediaVolume = vol2 / 15
                    fadeSoundSlowly(null, minusMediaVolume)
                }
            }
        })
        lifecycleScope.launch {
            viewModel.apply {


                durationMetaDataFlow.collectLatest {
                    if (backMaxDuration != null && affirmationMaxDuration != null && musicDetails?.affirmationIntroduction.isNullOrEmpty()) {
                        if ((backMaxDuration
                                ?: 0) > (viewModel.affirmationMaxDuration
                                ?: 0L) && viewModel.isAffirmationIntroduction.not() && viewModel.isBackgroundVolumeInverse.not() && viewModel.musicDetails?.isCustomizationEnabled != true
                        ) {
                            /** if selected sound duration is greater than affirmation and its not introduction playing right now and previously
                             * before background sound was not on main
                             **/
                            viewModel.apply {

                                viewModel.swapVolumeChanges()
                                mediaController?.addMediaData(
                                    mediaUri = backgroundUrl.orEmpty().toUri(),
                                    title = musicDetails?.musicTitle,
                                    artist = null,
                                    playWhenReady = true,
                                    image = getImageUrl(
                                        category = viewModel.categoryName,
                                        image = viewModel.musicDetails?.musicBackground
                                    )
                                )

                                backgroundMusicPlayer?.addBackgroundMedia(
                                    mediaUri = mainAudioUrl.orEmpty().toUri(),
                                    playWhenReady = mediaController?.musicPLayingState()?.value == true,
                                )
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {

            viewModel.apply {
                backgroundMusicPlayer?.musicPLayingState()?.collectLatest { isPlaying ->

                    if (seekWithSeekWithAffirmation != null && isPlaying) {
                        seekWithSeekWithAffirmation?.let {
                            seekWithSeekWithAffirmation = null
                            viewModel.backgroundMusicPlayer?.changePlayerProgress(it)
                        }
                    }
                }
            }
        }

        /** Play or pause button drawable change */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.mediaController?.musicPLayingState()?.collectLatest { isPlaying ->

                if (seekWithMusic != null && isPlaying) {

                    Log.e("playerProgress", "seeked $seekWithMusic")
                    seekWithMusic?.let {
                        seekWithMusic = null
                        viewModel.mediaController?.changeMusicProgress(it)
                    }
                }
                binding.ivPlayOrPause.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@PlayerActivity,
                        if (isPlaying) R.drawable.ic_music_pause else R.drawable.ic_music_play
                    )
                )

                /** Stopping background player if customization is enabled and countdown is finished */
                val backgroundMusicPlaying = if (
                    (viewModel.musicDetails?.isCustomizationEnabled == true)
                    && viewModel.isAffirmationIntroduction.not() &&
                    viewModel.musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == true
                ) {
                    viewModel.isBackgroundMusicPlaying && isPlaying
                } else isPlaying

                viewModel.backgroundMusicPlayer?.playOrPausePlayer(
                    isPlaying = backgroundMusicPlaying,
                    (viewModel.musicDetails?.affirmationIntroduction == null)
                )
                Log.e(
                    "prpk",
                    " viewModel.mediaController?.musicPLayingState() ${backgroundMusicPlaying}"
                )


                /** Stopping main player if customization is enabled and countdown is finished */
                val mainMusicPlaying = if (
                    viewModel.musicDetails?.isCustomizationEnabled == true
                    && viewModel.isAffirmationIntroduction.not()
                ) {
                    viewModel.isMainMusicPlaying && isPlaying
                } else isPlaying

                /** Stopping and playing the timer */
                if (viewModel.musicDetails?.isCustomizationEnabled == true
                    && viewModel.isAffirmationIntroduction.not()
                ) {

                    if (mainMusicPlaying) {
                        viewModel.settingCustomTimer()
                    } else viewModel.countDownTimer?.cancel()

                    if (backgroundMusicPlaying) {
                        viewModel.backgroundTimer()
                    } else viewModel.backgroundCountDownTimer?.cancel()
                }

                /**
                 * Stopping the player if customize is enabled and main timer is stopped when played from the notification
                 * */
                if (isPlaying &&
                    viewModel.isMainMusicPlaying.not() &&
                    viewModel.isAffirmationIntroduction.not() &&
                    viewModel.musicDetails?.isCustomizationEnabled == true
                ) {
                    viewModel.mediaController?.playOrPauseMusic()
                }
            }
        }

        /** Seek bar progress */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.mediaController?.musicProgress()?.collectLatest { mediaProgress ->

                viewModel.musicStart.set(mediaProgress.musicProgress)
                binding.musicPlayerSlider.progress = mediaProgress.seekBarProgress.toInt()

                /** Affirmation session initialization */
                if (mediaProgress.seekBarProgress > 150) {
                    viewModel.musicCurrentProgressInSeconds =
                        (mediaProgress.seekBarProgress.toInt() / 1000)
                    if (mediaProgress.seekBarProgress >= binding.musicPlayerSlider.max) {
                        viewModel.isMusicComplete = 1
                        viewModel.streakCount += 1
                    }
                }
                if (viewModel.backgroundMusicPlayer?.musicPLayingState()?.value == false && viewModel.backgroundMusicPlayer!!.getPlayBackState().value == Player.STATE_ENDED && viewModel.mediaController!!.musicRepeated().value && mediaProgress.seekBarProgress < 1000) {
                    viewModel.backgroundMusicPlayer?.changePlayerProgress(
                        progress = mediaProgress.seekBarProgress
                    )
                    Log.e("background repeat", "observer: ")

                }

                /** Removing introduction and playing affirmation */
                if (mediaProgress.seekBarProgress > 150 && (mediaProgress.seekBarProgress) >= (binding.musicPlayerSlider.max - 200)
                    && viewModel.isAffirmationIntroduction
                ) {
                    viewModel.isAffirmationIntroduction = false
                    viewModel.musicTitle.set(
                        viewModel.musicDetails?.musicTitle
                    )
                    removeIntroAddAffirmation()
                    backgroundMusicPlayVisibility()
                    affirmationCustomTime()
                }
            }
        }


        /** Music end time */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.mediaController?.musicEndTime()?.collectLatest { mediaProgress ->
                viewModel.musicEnd.set(mediaProgress.musicProgress)
                binding.musicPlayerSlider.max = mediaProgress.seekBarProgress.toInt()
            }
        }

        /** Play or pause button drawable change */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.mediaController?.musicRepeated()?.collectLatest { isRepeated ->
                binding.ivLoop.setColorFilter(
                    ContextCompat.getColor(
                        this@PlayerActivity,
                        if (isRepeated && viewModel.musicDetails?.isCustomizationEnabled == false) {
                            R.color.prime_blue_418FF6
                        } else R.color.white
                    )
                )
            }
        }

        /** Buffering */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.mediaController?.musicBuffering()?.collectLatest { isBuffering ->
                binding.buffering.visibility(
                    isVisible = isBuffering
                )
            }
        }

        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@PlayerActivity,
                    success = { _ ->
                        viewModel.musicDetails?.musicFavouriteStatus =
                            if (viewModel.musicDetails?.musicFavouriteStatus == 0) 1 else 0
                        isFavourite()
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
            viewModel.sessionEnd.collectLatest {
                handleResponse(
                    response = it,
                    context = this@PlayerActivity,
                    success = { _ ->
                        popUpWithFavResult()
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
            viewModel.musicCategories.collectLatest {
                handleResponse(
                    response = it,
                    context = this@PlayerActivity,
                    showToast = false,
                    success = { response ->
                        viewModel.musicCategoriesData = response
                        synchronized(this) {
                            musicCategoriesBottomSheet()
                        }
                    },
                    errorBlock = {
                        viewModel.musicCategoriesData = null
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
            viewModel.musicList.collectLatest {
                handleResponse(
                    response = it,
                    context = this@PlayerActivity,
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

        lifecycleScope.launch {
            viewModel.downloadStatus.collectLatest { downloadStatus ->
                updatingDownloadingStatus(
                    downloadStatus = downloadStatus
                )
            }
        }

        lifecycleScope.launch {
            downloadStatusListener.getDownloadStatus().collectLatest { status ->
                lifecycleScope.launch {
                    try {

                        /** Not collecting download status if already downloaded */
                        if (viewModel.downloadStatus.value != AppConstant.DownloadStatus.DOWNLOAD_COMPLETE) {
                            val downloadStatus = gettingDownloadStatus(
                                downloadStatus = status,
                                downloadStarted = viewModel.isDownloadingStarted,
                                music = viewModel.musicDetails
                            )

                            /** Avoiding other download status if downloading in progress */
                            if (downloadStatus == AppConstant.DownloadStatus.DOWNLOADING) {
                                viewModel.isDownloadingStarted = true
                            }
                            if (downloadStatus == AppConstant.DownloadStatus.DOWNLOAD_COMPLETE) {
                                viewModel.isDownloadingStarted = false
                            }

                            viewModel.changeDownloadStatus(
                                downloadStatus = downloadStatus
                            )
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        viewModel.changeDownloadStatus(
                            downloadStatus = AppConstant.DownloadStatus.NOT_DOWNLOAD
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModelAffirmationDetail.shareUrl.collectLatest {
                handleResponse(
                    response = it,
                    context = this@PlayerActivity,
                    success = { response ->
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("text/plain")
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        var shareMessage =
                            getString(R.string.this_is_an_awesome_app_it_will_help_you_to_feel_amazing_and_change_your_world_you_can_try_it_for_free_for_7_days_here_is_the_link)
                        shareMessage += "${response.url}"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))


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

    private fun removeIntroAddAffirmation() {
        if (viewModel.musicDetails?.isCustomizationEnabled?.not() == true && !viewModel.isBackgroundVolumeInverse && (viewModel.musicDetails?.duration?.roundToInt()
                ?: 0) > ((viewModel.introductionMaxDuration
                ?: 0) + (viewModel.affirmationMaxDuration ?: 0))
        ) {
            viewModel.apply {
                seekWithMusic = backgroundMusicPlayer?.musicProgress()?.value?.seekBarProgress
                viewModel.swapVolumeChanges()
                mediaController?.addMediaData(
                    mediaUri = backgroundUrl.orEmpty().toUri(),
                    title = musicDetails?.musicTitle,
                    artist = null,
                    playWhenReady = true,
                    image = getImageUrl(
                        category = viewModel.categoryName,
                        image = viewModel.musicDetails?.musicBackground
                    )
                )

                backgroundMusicPlayer?.addBackgroundMedia(
                    mediaUri = mainAudioUrl.orEmpty().toUri(),
                    playWhenReady = mediaController?.musicPLayingState()?.value == true,
                )
            }
        } else {

            viewModel.mediaController?.addMediaData(
                mediaUri = viewModel.mainAudioUrl.orEmpty().toUri(),
                title = viewModel.musicDetails?.musicTitle,
                artist = null,
                playWhenReady = true,
                image = getImageUrl(
                    category = viewModel.categoryName,
                    image = viewModel.musicDetails?.musicBackground
                )
            )
        }


    }

    private fun fadeSoundSlowly(minus: Float?, minusMediaVolume: Float?) {
        viewModel.apply {
            lifecycleScope.launch {
                if (minus!=null) {
                    val changeValue =
                        (((backgroundMusicPlayer?.getPlayerVolume()
                            ?: 0f) * 100) - minus).roundToInt()
                    backgroundMusicPlayer?.changePlayerVolume(changeValue)
                }


                if (minusMediaVolume!=null) {
                    val changeValueMediaController =
                        (((mediaController?.getPlayerVolume()
                            ?: 0f) * 100) - minusMediaVolume).roundToInt()
                    mediaController?.changeVolume(changeValueMediaController)
                }
                delay(1000)
                if (viewModel.customTime.get() != "00:00:00") {
                    fadeSoundSlowly(minus, minusMediaVolume)
                    Log.e("fade", "fadeSoundSlowly: ${minus}      -    ${minusMediaVolume}")
                }else{
                    binding.sliderVolume.progress=binding.sliderVolume.progress
                }
            }
        }
    }

    private suspend fun updatingDownloadingStatus(
        downloadStatus: AppConstant.DownloadStatus
    ) {
        withContext(Dispatchers.Main) {
            binding.clDownloadingStatus.visibility(
                isVisible = downloadStatus == AppConstant.DownloadStatus.DOWNLOADING || downloadStatus ==
                        AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
            )
            binding.ivDownloadComplete.visibility(
                isVisible = downloadStatus ==
                        AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
            )
            binding.downloadProgress.visibility(
                isVisible = downloadStatus ==
                        AppConstant.DownloadStatus.DOWNLOADING
            )
        }
    }

    /**
     * Setting the customization if enabled
     * @param isSettingAudioInPlayers to set audio in player true or false
     * */
    private fun affirmationCustomTime(
        isSettingAudioInPlayers: Boolean = true
    ) {

        if (viewModel.isAffirmationIntroduction.not() &&
            viewModel.musicDetails?.isCustomizationEnabled == true
        ) {
            binding.customTime.visibility(
                isVisible = viewModel.musicDetails?.isCustomizationEnabled == false
            )
            if (isSettingAudioInPlayers) {
                viewModel.apply {
                    val affirmationUrl =
                        if (isBackgroundMusicCustomTimeIsGreater()) backgroundUrl else mainAudioUrl
                    val backgroundUrl =
                        if (isBackgroundMusicCustomTimeIsGreater()) mainAudioUrl else backgroundUrl
                    if (isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && !viewModel.isBackgroundVolumeInverse) {
                        swapVolumeChanges()
                    } else if (!isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && viewModel.isBackgroundVolumeInverse) {
                        swapVolumeChanges()
                    }

                    mediaController?.addMediaData(
                        mediaUri = affirmationUrl.orEmpty().toUri(),
                        title = musicDetails?.musicTitle,
                        artist = null,
                        playWhenReady = true,
                        image = getImageUrl(
                            category = viewModel.categoryName,
                            image = viewModel.musicDetails?.musicBackground
                        )
                    )
                    backgroundMusicPlayer?.addBackgroundMedia(
                        mediaUri = backgroundUrl.orEmpty().toUri(),
                        playWhenReady = mediaController?.musicPLayingState()?.value == true,
                    )
                }
            }
            viewModel.settingCustomTimer()
            viewModel.backgroundTimer()
        }

    }

    /**
     * Music list bottom sheet and setting the selected background music and its details
     * */
    private fun musicListBottomSheet(data: MusicList?) {
        if (musicListBottomSheet?.dialog?.isShowing == true) {
            return
        }
        musicListBottomSheet = MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_LIST_DATA to if (data == null) null else Gson().toJson(data),
                AppConstant.IS_MUSIC_CATEGORY to false,
                AppConstant.TYPE to viewModel.categoryName
            ).also {
                AppConstant.REAL_CATEGORY.let { key ->
                    if (intent.hasExtra(key)) {
                        it.putString(key, intent.getStringExtra(key))
                    }
                }
            }
            show(
                supportFragmentManager, null
            )

            backgroundMusicUrl = { selectedBackgroundMusic ->
                viewModel.playingAudioAgain()
                viewModel.selectedBackgroundMusic = selectedBackgroundMusic

                settingSelectedBackgroundMusic(
                    title = selectedBackgroundMusic?.audioName.orEmpty(),
                    image = selectedBackgroundMusic?.thumbnail.orEmpty(),
                    musicUrl = selectedBackgroundMusic?.audio.orEmpty()
                )


                if ((selectedBackgroundMusic?.duration?.roundToInt()
                        ?: 0) > (viewModel.affirmationMaxDuration
                        ?: 0L) && viewModel.isAffirmationIntroduction.not() && viewModel.isBackgroundVolumeInverse.not() && viewModel.musicDetails?.isCustomizationEnabled != true
                ) {
                    /** if selected sound duration is greater than affirmation and its not introduction playing right now and previously
                     * before background sound was not on main
                     **/
                    viewModel.apply {
                        seekWithSeekWithAffirmation =
                            mediaController?.musicProgress()?.value?.seekBarProgress
                        viewModel.swapVolumeChanges()
                        mediaController?.addMediaData(
                            mediaUri = backgroundUrl.orEmpty().toUri(),
                            title = musicDetails?.musicTitle,
                            artist = null,
                            playWhenReady = true,
                            image = getImageUrl(
                                category = viewModel.categoryName,
                                image = viewModel.musicDetails?.musicBackground
                            )
                        )

                        backgroundMusicPlayer?.addBackgroundMedia(
                            mediaUri = mainAudioUrl.orEmpty().toUri(),
                            playWhenReady = mediaController?.musicPLayingState()?.value == true,
                        )
                    }

                } else if ((selectedBackgroundMusic?.duration?.roundToInt()
                        ?: 0) > (viewModel.affirmationMaxDuration
                        ?: 0L) && viewModel.isAffirmationIntroduction.not() && viewModel.isBackgroundVolumeInverse && viewModel.musicDetails?.isCustomizationEnabled != true
                ) {
                    viewModel.apply {
//                        mediaController?.addMediaData(
//                            mediaUri = backgroundUrl.orEmpty().toUri(),
//                            title = musicDetails?.musicTitle,
//                            artist = null,
//                            playWhenReady = true,
//                            image = getImageUrl(
//                                category = viewModel.categoryName,
//                                image = viewModel.musicDetails?.musicBackground
//                            )
//                        )

                        seekWithSeekWithAffirmation =
                            mediaController?.musicProgress()?.value?.seekBarProgress
                        mediaController?.addMediaData(
                            mediaUri = backgroundUrl.orEmpty().toUri(),
                            title = musicDetails?.musicTitle,
                            artist = null,
                            playWhenReady = true,
                            image = getImageUrl(
                                category = viewModel.categoryName,
                                image = viewModel.musicDetails?.musicBackground
                            )
                        )

                        backgroundMusicPlayer?.addBackgroundMedia(
                            mediaUri = mainAudioUrl.orEmpty().toUri(),
                            playWhenReady = mediaController?.musicPLayingState()?.value == true,
                        )
                    }
                } else if ((selectedBackgroundMusic?.duration?.roundToInt()
                        ?: 0) < (viewModel.affirmationMaxDuration
                        ?: 0L) && viewModel.isAffirmationIntroduction.not() && viewModel.isBackgroundVolumeInverse && viewModel.musicDetails?.isCustomizationEnabled != true
                ) {
                    restoreMainOnTop()
                } else if (viewModel.musicDetails?.isCustomizationEnabled != true && viewModel.isAffirmationIntroduction.not()) {
                    viewModel.backgroundMusicPlayer?.addBackgroundMedia(
                        viewModel.backgroundUrl.toString().toUri(),
                        viewModel.mediaController?.musicPLayingState()?.value == true
                    )
                }
                selectBackgroundTitle = selectedBackgroundMusic?.audioName.orEmpty()
                selectBackgroundImage = selectedBackgroundMusic?.thumbnail.orEmpty()
                selectBackgroundMusic = selectedBackgroundMusic?.audio.orEmpty()
                musicCategoriesBottomSheet?.dismiss()
            }
        }
    }

    private fun restoreMainOnTop() {


        viewModel.apply {
            seekWithMusic = backgroundMusicPlayer?.musicProgress()?.value?.seekBarProgress
            viewModel.swapVolumeChanges()
            mediaController?.addMediaData(
                mediaUri = mainAudioUrl.orEmpty().toUri(),
                title = musicDetails?.musicTitle,
                artist = null,
                playWhenReady = true,
                image = getImageUrl(
                    category = viewModel.categoryName,
                    image = viewModel.musicDetails?.musicBackground
                )
            )

            backgroundMusicPlayer?.addBackgroundMedia(
                mediaUri = backgroundUrl.orEmpty().toUri(),
                playWhenReady = mediaController?.musicPLayingState()?.value == true,
            )
        }


    }

    /**
     * Setting background music details and audio
     * @param title
     * @param image
     * @param musicUrl
     * @param setNewBackgroundMusic set true when want to change background music
     * */
    private fun settingSelectedBackgroundMusic(
        title: String,
        image: String,
        musicUrl: String,
        setNewBackgroundMusic: Boolean = true,
        setDefaultMusic: Boolean = false
    ) {
        viewModel.subTitle.set(title)
        if (title.isEmpty()) {
            binding.cvMusicPlay.inVisible()
        } else {
            binding.cvMusicPlay.visible()
        }
        binding.ivAffirmationProfileBg.glide(
            requestManager = requestManager,
            image = BuildConfig.MUSIC_BASE_URL.plus(image),
            placeholder = R.drawable.ic_error_place_holder,
            error = R.drawable.ic_error_place_holder
        )
        if (setNewBackgroundMusic) {
            viewModel.settingNewBackgroundMusic(
                music = musicUrl
            )
        }
        if (setDefaultMusic) {
            viewModel.setDefaultMusic(musicUrl)
        }
    }

    private fun musicCategoriesBottomSheet() {

        if (musicCategoriesBottomSheet?.dialog?.isShowing == true) {
            return
        }

        /** Stopping the spoken affirmation */
        if (viewModel.mediaController?.musicPLayingState()?.value == true) {
            viewModel.mediaController?.playOrPauseMusic()
        }

        /** Stopping background music play if affirmation is completed */
        viewModel.backgroundMusicPlayer?.playOrPausePlayer(
            isPlaying = false
        )

        musicCategoriesBottomSheet = MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_CATEGORIES_DATA to Gson().toJson(viewModel.musicCategoriesData),
                AppConstant.IS_MUSIC_CATEGORY to true,
                AppConstant.TYPE to viewModel.categoryName
            ).also {
                AppConstant.REAL_CATEGORY.let { key ->
                    if (intent.hasExtra(key)) {
                        it.putString(key, intent.getStringExtra(key))
                    }
                }
            }
            show(
                supportFragmentManager, null
            )
            closed = {
                viewModel.playingAudioAgain()
            }
            categoryId = { categoryId ->
                viewModel.categoryId = categoryId
                viewModel.retryApiRequest(
                    apiName = ApiConstant.MUSIC_LIST
                )
            }
        }
    }

    private fun gettingBundle() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryName = when (categoryName) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
                AppConstant.HomeCategory.MUSIC.name -> AppConstant.HomeCategory.MUSIC
                AppConstant.HomeCategory.DOWNLOAD.name -> AppConstant.HomeCategory.DOWNLOAD
                AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> AppConstant.HomeCategory.WISDOM_INSPIRATION
                else -> null
            }
        }
        Log.e("categoryName", "gettingBundle: ${viewModel.categoryName}")

        intent.getStringExtra(AppConstant.MUSIC_DETAILS)?.let { musicDetails ->
            viewModel.musicDetails = Gson().fromJson(musicDetails, MusicDetails::class.java)
            viewModel.isAffirmationIntroduction =
                viewModel.musicDetails?.affirmationIntroduction.isNullOrEmpty().not()
            settingPlayerBackground(
                isDarkBackground = false
            )

            isFavourite()
            viewModel.musicTitle.set(
                if (viewModel.isAffirmationIntroduction) getString(R.string.affirmation_introduction) else viewModel.musicDetails?.musicTitle
            )
            viewModel.subTitle.set(viewModel.musicDetails?.backgroundMusicTitle.orEmpty())
            viewModel.defaultMusicUrl = viewModel.musicDetails?.backgroundMusicUrl.toString()
            viewModel.defaultMusicImage = viewModel.musicDetails?.musicBackground.toString()
            viewModel.defaultMusicTitle = viewModel.musicDetails?.backgroundMusicTitle.toString()

        }
        backgroundMusicPlayVisibility()
        binding.ivMenu.visibility(
            isVisible = viewModel.categoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    viewModel.categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION ||
                    viewModel.musicDetails?.isSleep ?: false
        )
        binding.ivDownload.visibility(
            isVisible = (viewModel.categoryName == AppConstant.HomeCategory.MUSIC)
        )
        binding.ivFav.visibility(
            isVisible = viewModel.categoryName != AppConstant.HomeCategory.DOWNLOAD
        )
        if (viewModel.categoryName == AppConstant.HomeCategory.DOWNLOAD ||
            viewModel.categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION
        ) {
            binding.ivShare.visibility =
                View.INVISIBLE
        }


        if (viewModel.musicDetails?.backgroundMusicTitle?.isBlank() == true || viewModel.categoryName == AppConstant.HomeCategory.MUSIC) {
            binding.cvMusicPlay.inVisible()
        } else {
            binding.cvMusicPlay.visible()
        }


    }

    /**
     * Setting player background
     * @param isDarkBackground if enabled set dark background for the player
     * */
    private fun settingPlayerBackground(
        isDarkBackground: Boolean
    ) {

        val color = if (isDarkBackground) R.color.black else android.R.color.transparent
        binding.ivPlayerBg.setColorFilter(
            ContextCompat.getColor(
                this,
                color
            )
        )

        val image = getImageUrl(
            category = viewModel.categoryName,
            image = viewModel.musicDetails?.musicBackground
        )

        if (viewModel.musicDetails?.musicBackground?.takeLast(3) == "svg") {
            binding.ivPlayerBg.loadSvg(
                imageLoader = imageLoader,
                imageRequest = imageRequest,
                url = image,
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )
        } else {
            binding.ivPlayerBg.glide(
                requestManager = requestManager,
                image = image,
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )
        }
    }

    /**
     * Showing and hiding the audio balance and background music view visibility
     * */
    private fun backgroundMusicPlayVisibility() {
        val isBackgroundMusicEnabled = if (viewModel.musicDetails?.isCustomizationEnabled == true) {
            viewModel.musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == true
        } else true

        val isVisible = ((viewModel.categoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                viewModel.categoryName == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                viewModel.categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION ||
                viewModel.categoryName == AppConstant.HomeCategory.WISDOM_INSPIRATION) &&
                isBackgroundMusicEnabled)
        binding.cvMusicPlay.visibility(
            isVisible = isVisible
        )
        binding.secondaryVolume.visibility(
            isVisible = isVisible
        )
    }

    private fun isFavourite() {
        binding.ivFav.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (viewModel.musicDetails?.musicFavouriteStatus == 0) {
                    R.drawable.ic_player_fav_unlike
                } else R.drawable.ic_player_fav_like
            )
        )
    }


    /** Play button working in customization */
    private fun playCustomMusic() {
        if (viewModel.musicDetails?.isCustomizationEnabled == true
            && viewModel.isAffirmationIntroduction.not() && viewModel.isMainMusicPlaying
        ) {
            viewModel.mediaController?.playOrPauseMusic()
            return
        }
        viewModel.mediaController?.playOrPauseMusic()
    }

    private fun onClick() {
        binding.cvMusicPlay.setOnClickListener {
            if (viewModel.musicDetails?.ableToChangeBackgroundMusic == true) {
                if (viewModel.musicCategoriesData == null) {
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.MUSIC_CATEGORIES
                    )
                    return@setOnClickListener
                }
                musicCategoriesBottomSheet()
            }
        }

        binding.ivBack.setOnClickListener {
            endSession()
        }

        binding.ivMenu.setOnClickListener {
            if (customiseAffirmationFragment?.dialog?.isShowing == true) {
                return@setOnClickListener
            }
            customiseAffirmationFragment = CustomiseAffirmationFragment { data ->


                viewModel.musicDetails?.musicCustomizeDetail = data
                if (viewModel.categoryName == AppConstant.HomeCategory.GUIDED_MEDITATION) {
                    val timeHm = formatSecondsToHhMm(
                        viewModel.musicDetails?.duration?.roundToInt()?.toLong() ?: 0L
                    )
                    viewModel.musicDetails?.musicCustomizeDetail?.affirmationHour =
                        timeHm.split(":")[0].toInt()

                    viewModel.musicDetails?.musicCustomizeDetail?.affirmationMinute =
                        timeHm.split(":")[1].toInt()

                    viewModel.musicDetails?.musicCustomizeDetail?.affirmationSeconds =
                        timeHm.split(":")[2].toInt()
                }
                viewModel.customTimeLong = null
                viewModel.backgroundCustomTimeLong = null
                viewModel.musicDetails?.isCustomizationEnabled = true
                settingPlayerBackground(isDarkBackground = data?.darkMode ?: false)
                if (data != null) {
                    if (data.defaultMusicUrl) {
                        viewModel.selectedBackgroundMusic = null
                        settingSelectedBackgroundMusic(
                            title = viewModel.defaultMusicTitle.orEmpty(),
                            image = viewModel.defaultMusicImage.orEmpty(),
                            musicUrl = viewModel.defaultMusicUrl.orEmpty(),
                            setNewBackgroundMusic = false,
                            setDefaultMusic = true
                        )
                    }
                }
                affirmationCustomTime()
                /** Changing player and volume slider to 50 */
                binding.sliderVolume.progress = 50
                viewModel.backgroundMusicPlayer?.changePlayerVolume(
                    volume = 50
                )
                viewModel.mediaController?.changeVolume(
                    volume = 50
                )

            }.apply {
                arguments = bundleOf(
                    AppConstant.CATEGORY_NAME to viewModel.categoryName?.name,
                    AppConstant.PLAYER to true,
                    AppConstant.MUSIC_CUSTOMIZE_DETAIL to Gson().toJson(viewModel.musicDetails?.musicCustomizeDetail),
                    AppConstant.CUSTOMIZE to viewModel.musicDetails?.isCustomizationEnabled,
                    AppConstant.DU to viewModel.musicDetails?.duration.toString()
                )
                    .also {
                        if (intent.hasExtra(AppConstant.REAL_CATEGORY)) {
                            it.putString(
                                AppConstant.REAL_CATEGORY,
                                intent.getStringExtra(AppConstant.REAL_CATEGORY)
                            )
                        }
                    }
                show(
                    supportFragmentManager, null
                )
            }
        }
        binding.ivPlayOrPause.setOnClickListener {
            /** Play button working in customization */
            playCustomMusic()
        }

        binding.ivForward10.setOnClickListener {
            val progress = binding.musicPlayerSlider.progress
            viewModel.mediaController?.changeMusicProgress(
                progress = progress.toLong() + 10000L
            )
            viewModel.backgroundMusicPlayer?.changePlayerProgress(
                progress = progress.toLong() + 10000L
            )
        }

        binding.ivBack10.setOnClickListener {
            val progress = binding.musicPlayerSlider.progress
            viewModel.mediaController?.changeMusicProgress(
                progress = progress.toLong() - 10000L
            )
            viewModel.backgroundMusicPlayer?.changePlayerProgress(
                progress = progress.toLong() - 10000L
            )
        }

        binding.ivLoop.setOnClickListener {
            if (viewModel.isAffirmationIntroduction ||
                viewModel.musicDetails?.isCustomizationEnabled == true
            ) {
                return@setOnClickListener
            }
            viewModel.mediaController?.musicRepeat()
        }

        binding.musicPlayerSlider.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    viewModel.mediaController?.changeMusicProgress(
                        progress = p1.toLong()
                    )
                    viewModel.backgroundMusicPlayer?.changePlayerProgress(
                        progress = p1.toLong()
                    )
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.sliderVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {

                    if (viewModel.isBackgroundVolumeInverse) {
                        viewModel.backgroundMusicPlayer?.changePlayerVolume(
                            volume = 100 - p1
                        )
                        viewModel.mediaController?.changeVolume(
                            volume = p1
                        )
                        return
                    }

                    viewModel.backgroundMusicPlayer?.changePlayerVolume(
                        volume = p1
                    )
                    viewModel.mediaController?.changeVolume(
                        volume = 100 - p1
                    )
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.ivFav.setOnClickListener {
            viewModel.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }

        binding.ivDownload.setOnClickListener {
            lifecycleScope.launch {
                val downloadedMusicListSize = withContext(Dispatchers.IO) {
                    roomDataBaseInterface.getList().size
                }
                if (downloadedMusicListSize >= 10) {
                    showToast(
                        message = getString(R.string.download_limit_exceeded)
                    )
                    return@launch
                }

                if (viewModel.downloadStatus.value == AppConstant.DownloadStatus.NOT_DOWNLOAD) {
                    if (isBackgroundSelect == true) {
                        viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicUrl =
                            selectBackgroundMusic
                        viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicImage =
                            selectBackgroundImage
                        viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicTitle =
                            selectBackgroundTitle
                        viewModel.musicDetails?.isCustomizationEnabled = true
                        viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicHour =
                            viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicHour
                        viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicMinute =
                            viewModel.musicDetails?.musicCustomizeDetail?.backgroundMusicMinute
                        viewModel.musicDetails?.musicCustomizeDetail?.affirmationHour =
                            viewModel.musicDetails?.musicCustomizeDetail?.affirmationHour
                        viewModel.musicDetails?.musicCustomizeDetail?.affirmationMinute =
                            viewModel.musicDetails?.musicCustomizeDetail?.affirmationMinute

                        downloadWithWorkManager(
                            category = viewModel.categoryName,
                            musicDetails = viewModel.musicDetails,
                            activity = this@PlayerActivity
                        )
                    } else {
                        downloadWithWorkManager(
                            category = viewModel.categoryName,
                            musicDetails = viewModel.musicDetails,
                            activity = this@PlayerActivity
                        )


                    }
                    getString(R.string.original_music_will_be).showSnackBar(binding.root)
                }
            }
        }

        binding.ivShare.setOnClickListener {
            viewModelAffirmationDetail.shareId = viewModel.musicDetails?.musicId.toString()
            viewModelAffirmationDetail.shareType =
                when (viewModel.categoryName?.name) {
                    AppConstant.HomeCategory.GUIDED_AFFIRMATION.toString() -> ApiConstant.ExploreType.AFFIRMATION.value
                    AppConstant.HomeCategory.GUIDED_MEDITATION.toString() -> ApiConstant.ExploreType.MEDITATION.value
                    AppConstant.HomeCategory.MUSIC.toString() -> ApiConstant.ExploreType.MUSIC.value
                    else -> "3"
                }
            viewModelAffirmationDetail.retryApiRequest(ApiConstant.SHARE_URL)

        }
    }

    private fun endSession() {
        when (viewModel.categoryName) {
            AppConstant.HomeCategory.GUIDED_MEDITATION -> {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.GUIDED_MEDITATION_SESSION_END
                )
            }

            AppConstant.HomeCategory.GUIDED_AFFIRMATION -> {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.GUIDED_AFFIRMATION_SESSION_END
                )
            }

            AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.GUIDED_WISDOM_SESSION_END
                )
            }

            else -> popUpWithFavResult()
        }
    }
}
