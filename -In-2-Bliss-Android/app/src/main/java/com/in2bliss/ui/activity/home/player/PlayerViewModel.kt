package com.in2bliss.ui.activity.home.player

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.core.net.toUri
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.backgroundMusicPlayer.BackgroundMusicPlayerImpl
import com.in2bliss.data.mediaController.MediaControllerImpl
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import com.in2bliss.domain.MediaControllerInterface
import com.in2bliss.ui.activity.home.music.MusicAdapter
import com.in2bliss.ui.activity.home.music.MusicCategoryAdapter
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.convertTimeToMilliseconds
import com.in2bliss.utils.extension.countDown
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.getFavouriteHashMap
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.getMediaDuration
import com.in2bliss.utils.extension.gettingAudioUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    requestManager: RequestManager,
    private val apiHelperInterface: ApiHelperInterface,
) : BaseViewModel() {

    val title = ObservableField("Music")
    val subTitle = ObservableField("")
    var firstBackgroundMusic: MusicList.Data? = null
    var defaultMusicImage: String? = null
    var defaultMusicTitle: String? = null
    var defaultMusicUrl: String? = null
    val musicTitle = ObservableField("")
    val musicStart = ObservableField("00:00")
    val musicEnd = ObservableField("00:00")
    val customTime = ObservableField("")
    val customBackTime = ObservableField("")
    var categoryName: AppConstant.HomeCategory? = null
    var mainAudioUrl: String? = null
    var backgroundUrl: String? = null
    private var affirmationIntroductionUrl: String? = null
    var isDownloadingStarted = false

    var musicDetails: MusicDetails? = null
    var isAffirmationIntroduction = false
    var isBackgroundVolumeInverse = false
    var musicCurrentProgressInSeconds: Int = 0
    var isMusicComplete = 0
    var streakCount = 0
    var mediaController: MediaControllerInterface? = null
    var backgroundMusicPlayer: BackgroundMusicPlayerInterface? = null
    var musicCategoriesData: MusicCategories? = null
    var categoryId: Int? = 0
    var selectedBackgroundMusic: MusicList.Data? = null
    var isFavourite: Boolean? = null
    var favMusicId: Int? = null
    var position: Int? = null

    var countDownTimer: CountDownTimer? = null
    var customTimeLong: Long? = null
    var backgroundCountDownTimer: CountDownTimer? = null
    var backgroundCustomTimeLong: Long? = null
    var isBackgroundMusicPlaying = false
    var isMainMusicPlaying = true
    private var isRepeat = musicDetails?.isCustomizationEnabled

    private val mutableFavouriteAffirmation by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val favouriteAffirmation by lazy {
        mutableFavouriteAffirmation.asSharedFlow()
    }

    private val mutableSessionEnd by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val sessionEnd by lazy {
        mutableSessionEnd.asSharedFlow()
    }

    private val mutableMusicCategories by lazy {
        MutableSharedFlow<Resource<MusicCategories>>()
    }
    val musicCategories by lazy {
        mutableMusicCategories.asSharedFlow()
    }

    private val mutableMusicList by lazy {
        MutableSharedFlow<Resource<MusicList>>()
    }
    val musicList by lazy {
        mutableMusicList.asSharedFlow()
    }

    private val mutableDownloadStatus by lazy {
        MutableStateFlow(AppConstant.DownloadStatus.NOT_DOWNLOAD)
    }
    val downloadStatus by lazy { mutableDownloadStatus.asStateFlow() }

    fun changeDownloadStatus(
        downloadStatus: AppConstant.DownloadStatus
    ) {
        viewModelScope.launch {
            mutableDownloadStatus.emit(value = downloadStatus)
        }
    }

    val musicCategoriesAdapter = MusicCategoryAdapter()
    val musicAdapter = MusicAdapter(requestManager)

    /**
     * Initialize media controller instance
     * @param context
     * @param controller callback of media controller
     * @param addMediaData callback for adding media data
     * */
    private fun initializeMediaController(
        context: Context,
        controller: (mediaController: MediaController?) -> Unit,
        addMediaData: () -> Unit
    ) {
        if (mediaController == null) {
            mediaController = mediaControllerInstance()
        }
        mediaController?.initializeMediaController(
            context = context,
            controller = { musicMediaController ->
                controller.invoke(musicMediaController)
            },
            addMediaData = {
                addMediaData.invoke()
            }
        )
    }

    /**
     * Initializing background music player
     * @param context
     * */
    private fun initializeBackgroundPlayer(
        context: Context,
    ) {
        if (backgroundMusicPlayer == null) {
            backgroundMusicPlayer = backgroundPLayerInstance()
        }
        backgroundMusicPlayer?.initializePlayer(
            context = context
        )
    }

    private fun backgroundPLayerInstance(): BackgroundMusicPlayerInterface {
        return BackgroundMusicPlayerImpl()
    }

    private fun mediaControllerInstance(): MediaControllerInterface {
        return MediaControllerImpl()
    }

    /**
     * Session end api request
     * */
    private fun sessionEnd() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            when (categoryName) {
                AppConstant.HomeCategory.GUIDED_MEDITATION -> {
                    hashMap[ApiConstant.STATUS] = isMusicComplete.toString()
                    hashMap[ApiConstant.MEDITATION_ID] = musicDetails?.musicId.toString()
                    hashMap[ApiConstant.END_TIME] = musicCurrentProgressInSeconds.toString()
                    hashMap[ApiConstant.STREAK_COUNT] = streakCount.toString()
                }

                AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
                    hashMap[ApiConstant.WISDOM_ID] = musicDetails?.musicId.toString()
                }

                else -> hashMap[ApiConstant.AFFIRMATION_ID] = musicDetails?.musicId.toString()
            }

            hashMap[ApiConstant.DATE] = getCurrentDate()

            mutableSessionEnd.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    when (categoryName) {
                        AppConstant.HomeCategory.GUIDED_MEDITATION -> apiHelperInterface.guidedMeditationSessionEnd(
                            body = hashMap
                        )

                        AppConstant.HomeCategory.WISDOM_INSPIRATION -> apiHelperInterface.guidedWisdomSessionEnd(
                            body = hashMap
                        )

                        else -> apiHelperInterface.guidedAffirmationSessionEnd(
                            body = hashMap
                        )
                    }
                },
                apiName = when (categoryName) {
                    AppConstant.HomeCategory.GUIDED_MEDITATION -> ApiConstant.GUIDED_MEDITATION_SESSION_END
                    else -> ApiConstant.GUIDED_AFFIRMATION_SESSION_END
                }
            )
            mutableSessionEnd.emit(value = response)
        }
    }

    /**
     * Mark meditation as favourite
     * */
    private fun favouriteAffirmation() {
        networkCallIo {
            val hashMap = if (isFavourite != null && favMusicId != null) {
                getFavouriteHashMap(
                    category = categoryName,
                    isFavourite = isFavourite,
                    favMusicId = favMusicId
                )
            } else getFavouriteHashMap(
                category = categoryName,
                isFavourite = musicDetails?.musicFavouriteStatus == 1,
                favMusicId = musicDetails?.musicId,
                isSleep = musicDetails?.isSleep ?: false
            )

            mutableFavouriteAffirmation.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.favouriteAffirmation(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
            mutableFavouriteAffirmation.emit(value = response)
        }
    }

    private fun getMusicList() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            if (categoryId == 0) {
                hashMap[ApiConstant.CID] = ""
            } else {
                hashMap[ApiConstant.CID] = categoryId.toString()
            }

            mutableMusicList.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getMusicList(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.MUSIC_LIST
            )
            mutableMusicList.emit(value = response)
        }
    }

    fun swapVolumeChanges() {
        isBackgroundVolumeInverse = !isBackgroundVolumeInverse
        val mainVolume = mediaController?.getPlayerVolume()
        mediaController?.changeVolume(
            ((backgroundMusicPlayer?.getPlayerVolume() ?: 0f) * 100).toInt()
        )
        backgroundMusicPlayer?.changePlayerVolume(((mainVolume ?: 0f) * 100).toInt())
    }

    private fun getMusicCategories() {
        networkCallIo {
            mutableMusicCategories.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.getMusicCategories()
                },
                apiName = ApiConstant.MUSIC_CATEGORIES
            )
            mutableMusicCategories.emit(value = response)
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.FAVOURITE_AFFIRMATION -> favouriteAffirmation()
            ApiConstant.GUIDED_MEDITATION_SESSION_END -> sessionEnd()
            ApiConstant.GUIDED_AFFIRMATION_SESSION_END -> sessionEnd()
            ApiConstant.GUIDED_WISDOM_SESSION_END -> sessionEnd()
            ApiConstant.MUSIC_CATEGORIES -> getMusicCategories()
            ApiConstant.MUSIC_LIST -> getMusicList()
        }
    }

    var affirmationMaxDuration: Long? = null
    var introductionMaxDuration: Long? = null
    var backMaxDuration: Long? = null
    val durationMetaDataFlow by lazy {
        MutableSharedFlow<Boolean>()
    }

    /**
     * Initializing mediaController
     * */
    fun initializingPlayers(
        context: Context
    ) {
        mainAudioUrl = gettingAudioUrl(
            category = categoryName,
            audio = musicDetails?.musicUrl
        )
        /**
         * Changing base url to Music url because in create affirmation
         * */
        backgroundUrl = if (categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) {
            BuildConfig.MUSIC_BASE_URL.plus(musicDetails?.backgroundMusicUrl)
        } else gettingAudioUrl(
            category = categoryName,
            audio = musicDetails?.backgroundMusicUrl
        )



        affirmationIntroductionUrl = gettingAudioUrl(
            category = categoryName,
            audio = musicDetails?.affirmationIntroduction
        )

        /** Setting new background music if customization is enabled and background music is selected */
        if (musicDetails?.isCustomizationEnabled == true &&
            musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == true &&
            musicDetails?.musicCustomizeDetail?.backgroundMusicUrl.isNullOrEmpty().not()
        ) {
            backgroundUrl =
                BuildConfig.MUSIC_BASE_URL.plus(musicDetails?.musicCustomizeDetail?.backgroundMusicUrl.orEmpty())
        }

        /** Changing the music url according to customization */
        val affirmationAudio = when {
            (isAffirmationIntroduction) -> affirmationIntroductionUrl.orEmpty().toUri()
            (isBackgroundMusicCustomTimeIsGreater() &&
                    musicDetails?.isCustomizationEnabled == true &&
                    isAffirmationIntroduction.not()
                    ) -> backgroundUrl.orEmpty().toUri()

            else -> mainAudioUrl.orEmpty().toUri()
        }

        val backgroundAudio = when {
            (isBackgroundMusicCustomTimeIsGreater() && musicDetails?.isCustomizationEnabled == true && isAffirmationIntroduction.not()) -> mainAudioUrl.orEmpty()
                .toUri()

            else -> backgroundUrl.orEmpty().toUri()
        }
        if (isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && !isBackgroundVolumeInverse) {
            swapVolumeChanges()
        } else if (!isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && isBackgroundVolumeInverse) {
            swapVolumeChanges()
        }
        if (affirmationMaxDuration == null) {
            viewModelScope.launch(Dispatchers.IO) {
                getMediaDuration(mainAudioUrl!!)?.let {
                    affirmationMaxDuration = it / 1000
                    durationMetaDataFlow.emit(true)

                }
                Log.e("metaDataDuration", "affirmationMaxDuration -> $affirmationMaxDuration")
            }
        }
        if (backMaxDuration == null) {
            viewModelScope.launch(Dispatchers.IO) {

                getMediaDuration(backgroundUrl!!)?.let {
                    backMaxDuration = it / 1000
                    durationMetaDataFlow.emit(true)

                }
                Log.e(
                    "metaDataDuration",
                    "backMaxDuration -> $backMaxDuration  ${musicDetails?.duration}",
                )

            }
        }
        if (isAffirmationIntroduction && introductionMaxDuration == null) {
            viewModelScope.launch(Dispatchers.IO) {
                getMediaDuration(affirmationIntroductionUrl!!)?.let {
                    introductionMaxDuration = it / 1000
                    durationMetaDataFlow.emit(true)

                }

                Log.e("metaDataDuration", "introductionMaxDuration -> $introductionMaxDuration")

            }
        }

        /** Initializing media controller */
        initializeMediaController(
            context = context,
            controller = { _ ->
            },
            addMediaData = {
                mediaController?.addMediaData(
                    mediaUri = affirmationAudio,
                    title = if (isAffirmationIntroduction) context.getString(R.string.affirmation_introduction) else musicDetails?.musicTitle,
                    artist = null,
                    playWhenReady = false,
                    image = getImageUrl(
                        category = categoryName,
                        image = musicDetails?.musicBackground
                    )
                )
            }
        )

        /** Only work in customize session */
        if (musicDetails?.isCustomizationEnabled == true
            && musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == false
        ) {
            return
        }

        /** Initializing background player */
        initializeBackgroundPlayer(context = context)
        backgroundMusicPlayer?.addBackgroundMedia(
            mediaUri = backgroundAudio,
            playWhenReady = false
        )
        isRepeat?.let {
            backgroundMusicPlayer?.changeRepeatMode(
                isRepeat = it
            )
        }
    }

    /**
     * Changing the background music with selected background music
     * @param music new selected background music
     * */
    fun settingNewBackgroundMusic(
        music: String
    ) {
        if (musicDetails?.isCustomizationEnabled == true && isAffirmationIntroduction.not() && isMainMusicPlaying.not()) {
            return
        }
        backgroundUrl = BuildConfig.MUSIC_BASE_URL.plus(music)


        /** Changing the player if background music is customization time is greater */
        if (isBackgroundMusicCustomTimeIsGreater() && musicDetails?.isCustomizationEnabled == true && isAffirmationIntroduction.not()) {
            if (isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && !isBackgroundVolumeInverse) {
                swapVolumeChanges()
            } else if (!isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && isBackgroundVolumeInverse) {
                swapVolumeChanges()
            }
            mediaController?.addMediaData(
                mediaUri = backgroundUrl.orEmpty().toUri(),
                title = musicDetails?.musicTitle,
                artist = null,
                playWhenReady = true,
                image = getImageUrl(
                    category = categoryName,
                    image = musicDetails?.musicBackground
                )
            )
            return
        }


        backgroundMusicPlayer?.addBackgroundMedia(
            mediaUri = BuildConfig.MUSIC_BASE_URL.plus(music).toUri(),
            playWhenReady = (mediaController?.musicPLayingState()?.value ?: false)
        )
    }

    /**
     * Changing the background music with selected background music
     * @param music new selected background music
     * */
    fun setDefaultMusic(music: String) {
        if (musicDetails?.isCustomizationEnabled == true && isAffirmationIntroduction.not() && isMainMusicPlaying.not()) {
            return
        }
        backgroundUrl = gettingAudioUrl(
            category = categoryName,
            audio = music
        )
        /** Changing the player if background music is customization time is greater */
        if (isBackgroundMusicCustomTimeIsGreater() && musicDetails?.isCustomizationEnabled == true && isAffirmationIntroduction.not()) {

            if (isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && !isBackgroundVolumeInverse) {
                swapVolumeChanges()
            } else if (!isBackgroundMusicCustomTimeIsGreater() && !isAffirmationIntroduction && isBackgroundVolumeInverse) {
                swapVolumeChanges()
            }
            mediaController?.addMediaData(
                mediaUri = backgroundUrl.orEmpty().toUri(),
                title = musicDetails?.musicTitle,
                artist = null,
                playWhenReady = true,
                image = getImageUrl(category = categoryName, image = musicDetails?.musicBackground)
            )
            return
        }
        backgroundUrl?.toUri()
            ?.let {
                backgroundMusicPlayer?.addBackgroundMedia(
                    mediaUri = it,
                    playWhenReady = (mediaController?.musicPLayingState()?.value ?: false)
                )
            }
    }

    /**
     *  Call when custom timer is used and showing countDownTimer
     *  */
    fun settingCustomTimer() {
        viewModelScope.launch(Dispatchers.Main) {
            val hour = if (isBackgroundMusicCustomTimeIsGreater()) {
                musicDetails?.musicCustomizeDetail?.backgroundMusicHour
            } else musicDetails?.musicCustomizeDetail?.affirmationHour

            val minute = if (isBackgroundMusicCustomTimeIsGreater()) {
                musicDetails?.musicCustomizeDetail?.backgroundMusicMinute
            } else musicDetails?.musicCustomizeDetail?.affirmationMinute

            val timeInMilliSeconds = if (customTimeLong != null) {
                customTimeLong ?: 0L
            } else convertTimeToMilliseconds(
                hour = hour,
                minutes = minute,
                if (!isBackgroundMusicCustomTimeIsGreater()) {
                    musicDetails?.musicCustomizeDetail?.affirmationSeconds
                } else {
                    0
                }
            )
            countDownTimer?.cancel()
            countDownTimer(
                timeInLong = timeInMilliSeconds
            )
        }
    }

    /**
     *  Call when custom timer is used and showing background countDownTimer
     * */
    fun backgroundTimer() {

        val backgroundHour = if (isBackgroundMusicCustomTimeIsGreater()) {
            musicDetails?.musicCustomizeDetail?.affirmationHour
        } else musicDetails?.musicCustomizeDetail?.backgroundMusicHour

        val backgroundMinute = if (isBackgroundMusicCustomTimeIsGreater()) {
            musicDetails?.musicCustomizeDetail?.affirmationMinute
        } else musicDetails?.musicCustomizeDetail?.backgroundMusicMinute

        val backgroundTimeInMilliSeconds = if (backgroundCustomTimeLong != null) {
            backgroundCustomTimeLong ?: 0L

        } else convertTimeToMilliseconds(
            hour = backgroundHour,
            minutes = backgroundMinute
        )

        backgroundCountDownTimer?.cancel()

        backgroundCountDown(
            timeInLong = backgroundTimeInMilliSeconds
        )
    }

    /**
     * Main count down for stopping music
     * @param timeInLong
     * */
    private fun countDownTimer(timeInLong: Long) {

        if (mediaController?.musicPLayingState()?.value == false) {
            mediaController?.playOrPauseMusic()
        }
        if (mediaController?.musicRepeated()?.value == false) {
            mediaController?.musicRepeat()
        }
        countDownTimer?.cancel()

        countDown(
            countDownTimeInLong = timeInLong,
            countDownTime = { countDown, hour, minute, seconds ->
                customTimeLong = countDown
                isMainMusicPlaying = true
                customTime.set(
                    "${String.format("%02d", hour)}:${
                        String.format("%02d", minute)
                    }:${String.format("%02d", seconds)}"
                )
            },
            isFinished = {
                isMainMusicPlaying = false
                customTimeLong = null
                if (mediaController?.musicPLayingState()?.value == true) {
                    mediaController?.playOrPauseMusic()
                    if (mediaController?.musicRepeated()?.value == true) {
                        mediaController?.musicRepeat()
                    }
                }
            },
            countDownTimerInstance = { countDown ->
                countDownTimer = countDown
            }
        )
    }

    /**
     * Background count down for stopping music not showing the countDown anywhere
     * @param timeInLong
     * */
    private fun backgroundCountDown(
        timeInLong: Long
    ) {

        countDown(
            countDownTimeInLong = timeInLong,
            countDownTime = { countDown, hour, minute, seconds  ->
                backgroundCustomTimeLong = countDown
                isBackgroundMusicPlaying = true
                backgroundMusicPlayer?.playOrPausePlayer(
                    isPlaying = mediaController?.musicPLayingState()?.value == true
                )
                customBackTime.set(
                    "${String.format("%02d", hour)}:${
                        String.format("%02d", minute)
                    }:${String.format("%02d", seconds)}"
                )
                Log.e("prpk", "backgroundCountDown: ")
            },
            isFinished = {
                isBackgroundMusicPlaying = false
                backgroundCustomTimeLong = null
                backgroundMusicPlayer?.playOrPausePlayer(
                    isPlaying = false
                )
            },
            countDownTimerInstance = { countDown ->
                backgroundCountDownTimer = countDown
            }
        )
    }

    /**
     * Is background music customize time is greater than affirmation audio only when customization is enabled
     * */
    fun isBackgroundMusicCustomTimeIsGreater(): Boolean {
        val affirmationTime = convertTimeToMilliseconds(
            hour = musicDetails?.musicCustomizeDetail?.affirmationHour,
            minutes = musicDetails?.musicCustomizeDetail?.affirmationMinute
        )
        val backgroundTime = convertTimeToMilliseconds(
            hour = musicDetails?.musicCustomizeDetail?.backgroundMusicHour,
            minutes = musicDetails?.musicCustomizeDetail?.backgroundMusicMinute
        )
        return if (
            musicDetails?.musicCustomizeDetail?.isBackgroundMusicEnabled == false || musicDetails?.isCustomizationEnabled == false
        ) false else backgroundTime > affirmationTime
    }

    /**
     * Playing audio again if bottom sheet is closed
     * */
    fun playingAudioAgain() {

        /** Play button working in customization */
        if (musicDetails?.isCustomizationEnabled == true
            && isAffirmationIntroduction.not()
        ) {

            if (isMainMusicPlaying) {
                mediaController?.playOrPauseMusic()
            }
            return
        }
        mediaController?.playOrPauseMusic()

        val backgroundMusicPlaying =
            if (musicDetails?.isCustomizationEnabled == true
                && isAffirmationIntroduction.not()
            ) {
                isBackgroundMusicPlaying && mediaController?.musicPLayingState()?.value == true
            } else mediaController?.musicPLayingState()?.value == true

        backgroundMusicPlayer?.playOrPausePlayer(
            isPlaying = backgroundMusicPlaying
        )
    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.releaseMediaController()
        backgroundMusicPlayer?.releasePlayer()
    }
}

