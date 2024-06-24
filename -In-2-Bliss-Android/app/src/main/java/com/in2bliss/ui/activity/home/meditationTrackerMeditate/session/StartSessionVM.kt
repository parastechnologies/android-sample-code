package com.in2bliss.ui.activity.home.meditationTrackerMeditate.session

import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import androidx.core.net.toUri
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.backgroundMusicPlayer.BackgroundMusicPlayerImpl
import com.in2bliss.data.model.meditationTracker.MediationTrackerModel
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import com.in2bliss.utils.extension.convertTimeToMilliseconds
import com.in2bliss.utils.extension.countDown
import com.in2bliss.utils.extension.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartSessionVM @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val time = ObservableField("")
    val title = ObservableField("Music")
    val subTitle = ObservableField("")
    val isLoading by lazy {
        MutableSharedFlow<Boolean>()
    }

    var musicPlayer: BackgroundMusicPlayerInterface? = null
    private var alarmPlayer: BackgroundMusicPlayerInterface? = null
    private var countDown: CountDownTimer? = null

    var meditation: MediationTrackerModel? = null
    private var timeInSeconds = 0L

    private val mutableSelfMeditationSessionResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val selfMeditationSessionResponse by lazy { mutableSelfMeditationSessionResponse.asSharedFlow() }

    /**
     * Initializing music music player
     * @param context
     * */
    private fun initializeMusicPlayer(
        context: Context,
    ) {

        if (musicPlayer == null) {
            musicPlayer = backgroundPLayerInstance()
        }
        musicPlayer?.initializePlayer(
            context = context
        )
    }

    /**
     * Initializing alarm music player
     * @param context
     * */
    private fun initializeAlarmPlayer(
        context: Context,
    ) {
        if (alarmPlayer == null) {
            alarmPlayer = backgroundPLayerInstance()
        }
        alarmPlayer?.initializePlayer(
            context = context
        )
    }

    private fun backgroundPLayerInstance(): BackgroundMusicPlayerInterface {
        return BackgroundMusicPlayerImpl()
    }

    private fun selfMediationSessionEnd() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            if (meditation?.musicId != null) {
                hashMap[ApiConstant.MID] = meditation?.musicId.toString()
            }
            hashMap[ApiConstant.END_TIME] = (timeInSeconds / 1000).toString()
            hashMap[ApiConstant.DATE] = getCurrentDate()

            mutableSelfMeditationSessionResponse.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.selfMeditationSessionEnd(hashMap)
                }, ApiConstant.SELF_MEDITATION_SESSION_END
            )
            mutableSelfMeditationSessionResponse.emit(value = response)
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.SELF_MEDITATION_SESSION_END -> selfMediationSessionEnd()
        }
    }

    /**
     * Setting the mediation data
     * */
    fun setSilentMeditation(
        context: Context
    ) {

        musicPlayerInitialize(
            musicUrl = meditation?.musicUrl.orEmpty(),
            context = context
        )
        timeInSeconds = convertTimeToMilliseconds(meditation?.hours, meditation?.min)

        viewModelScope.launch {

            if (meditation?.musicUrl.isNullOrEmpty()){
                startCountDown(context)
            }else {
                musicPlayer?.musicPLayingState()?.collectLatest {
                    if (it) {
                        isLoading.emit(false)
                        startCountDown(context)
                    }
                }
            }
        }


    }

    private fun startCountDown(context: Context) {
        countDown(
            countDownTimeInLong = timeInSeconds,
            countDownTime = { _, hour, minute, seconds ->
                time.set(
                    "${String.format("%02d", hour)}:${
                        String.format("%02d", minute)
                    }:${String.format("%02d", seconds)}"
                )
                if (meditation?.playEndBell == true) {

                    if (hour == 0 && minute == 0 && seconds == 7 && alarmPlayer == null) {
                        musicAlarmInitialize(
                            context = context
                        )
                    }
                }
            },
            isFinished = {
                musicPlayer?.changePlayerVolume(100)
                alarmPlayer?.playOrPausePlayer(
                    isPlaying = false
                )
                musicPlayer?.playOrPausePlayer(
                    isPlaying = false
                )
                countDown?.cancel()

                retryApiRequest(
                    apiName = ApiConstant.SELF_MEDITATION_SESSION_END
                )
            }, countDownTimerInstance = { countDownInstance ->
                countDown = countDownInstance
            }
        )

    }

    /**
     * initialize the music player for background music [musicPlayerInitialize]
     * */
    private fun musicPlayerInitialize(
        musicUrl: String,
        context: Context
    ) {
        initializeMusicPlayer(
            context = context
        )
        viewModelScope.launch {
            isLoading.emit(true)
        }

        musicPlayer?.addBackgroundMedia(
            mediaUri = (BuildConfig.MUSIC_BASE_URL + musicUrl).toUri(),
            playWhenReady = true
        )
        musicPlayer?.changeRepeatMode(
            isRepeat = true
        )
    }

    /**
     * initialize the alarm player for background music [musicAlarmInitialize]
     * */
    private fun musicAlarmInitialize(
        context: Context
    ) {
        initializeAlarmPlayer(
            context = context
        )
        alarmPlayer?.addBackgroundMedia(
            mediaUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.bell_small),
            playWhenReady = true
        )
        alarmPlayer?.changeRepeatMode(
            isRepeat = true
        )
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer?.releasePlayer()
        alarmPlayer?.releasePlayer()
    }
}

