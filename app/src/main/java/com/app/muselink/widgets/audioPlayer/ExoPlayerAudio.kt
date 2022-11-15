package com.app.muselink.widgets.audioPlayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Message

import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.app.muselink.R
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.util.SyncConstants
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ExoPlayerAudio(
    val context: Context,
    val exoPlayerAudioNavigator: ExoPlayerAudioNavigator,
    val songType: String
) {

    companion object {
        var currentSongPlayPos = 0
        var previousSongPlayPos = 0
    }

    interface ExoPlayerAudioNavigator {
        fun onSongCompleted()
        fun onPlayerReady()
        fun onNextSongPlayed()
        fun nextSongNotExist()
        fun playPreviousSound()
        fun previousSongNotExist()
        fun repeatSong()
        fun getCurrentSongPos(value: String?)
        fun updateProgress(value: Float)
    }

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var IsSongplaying = false

    @SuppressLint("HandlerLeak")
    private val mTimerHandler = Handler()


    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler()    {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                try {
                    exoPlayerAudioNavigator.getCurrentSongPos(getCurrentDuration())
                    val progress = (player?.currentPosition!! * 100) / player?.duration!!
                    exoPlayerAudioNavigator.updateProgress(progress.toFloat())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var timerStarted = false

    fun startTimer() {
        try {
            timerStarted = true
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    mTimerHandler.post(Runnable {
                        try {
                            handler.sendEmptyMessage(0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
            }
            timer?.schedule(timerTask, 0, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
            timer = null
        }
    }


    private var player: SimpleExoPlayer? = null
    private var playerPrepared: Boolean = false

    fun getAudioPlayer(): SimpleExoPlayer {
        return player!!
    }

    fun pausePlayAudio() {
        IsSongplaying = false
        player?.playWhenReady = false
    }

    fun playeraudioSeekTo(position: Long) {
        player?.seekTo(position)
//        startTimerProgress()
    }


    fun startPlayAudio() {
        player?.playWhenReady = true
//        startTimerProgress()
    }

    fun preparePlayerAndStart() {
        IsSongplaying = true
        playerPrepared = true
        player?.prepare(createMediaSource())
        startPlayAudio()
    }

    fun stopPlayer() {
        IsSongplaying = false
        stopTimer()
        pausePlayAudio()
    }

    fun preparePlayeForSingleSong(audioFile: String,isFile: Boolean) {
        playerPrepared = true

        Log.d("asdasdasdadsad","ffffff"+audioFile.toString())

        player?.prepare(createMediaSourceSingleSong(audioFile,isFile))
    }

    fun preparePlayer() {
        playerPrepared = true
        player?.prepare(createMediaSource())
    }

    fun intializeChnagedSonInPlayer() {
        if (listSongs.size - 1 > currentSongPlayPos) {
            SingletonInstances.currentAudioFilePlay = listSongs[currentSongPlayPos]
            SingletonInstances.currentAudioFilePlayPos = currentSongPlayPos
            preparePlayer()
        }
    }

    fun playPrevioussong() {

        if (currentSongPlayPos > 0) {
            previousSongPlayPos = currentSongPlayPos
            currentSongPlayPos = currentSongPlayPos - 1
            SingletonInstances.currentAudioFilePlay = listSongs[currentSongPlayPos]
            SingletonInstances.currentAudioFilePlayPos = currentSongPlayPos
            preparePlayer()
            exoPlayerAudioNavigator.onNextSongPlayed()
        } else {
            exoPlayerAudioNavigator.nextSongNotExist()
        }

//        if (currentSongPlayPos <= listSongs.size) {
//            currentSongPlayPos = currentSongPlayPos - 1
//            SingeltonInstances.currentAudioFilePlay = listSongs[currentSongPlayPos]
//            SingeltonInstances.currentAudioFilePlayPos = currentSongPlayPos
//            preparePlayer()
//            exoPlayerAudioNavigator.onNextSongPlayed()
//        } else {
//            exoPlayerAudioNavigator.nextSongNotExist()
////            goToPreviousOrBeginning()
//        }
    }

    fun playNextSong() {
        if (listSongs.size - 1 > currentSongPlayPos) {
            previousSongPlayPos = currentSongPlayPos
            currentSongPlayPos = currentSongPlayPos + 1
            SingletonInstances.currentAudioFilePlay = listSongs[currentSongPlayPos]
            SingletonInstances.currentAudioFilePlayPos = currentSongPlayPos
            preparePlayer()
            exoPlayerAudioNavigator.onNextSongPlayed()
        } else {
            exoPlayerAudioNavigator.nextSongNotExist()
//            goToPreviousOrBeginning()
        }
    }

    private val playerEventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                exoPlayerAudioNavigator.onSongCompleted()
            } else if (playbackState == ExoPlayer.STATE_IDLE) {
                Log.e("", "")
            } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
                Log.e("", "")
            } else if (playbackState == ExoPlayer.STATE_READY) {
                if (!timerStarted) {
                    timerStarted = false
                    stopTimer()
                    startTimer()
                } else {
                    startTimer()
                }
            }
            exoPlayerAudioNavigator.onPlayerReady()
        }


        override fun onSeekProcessed() {
            super.onSeekProcessed()
            Log.e("Calling", "Calling")
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
            super.onTracksChanged(trackGroups, trackSelections)
            Log.e("Calling", "Calling1")
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            super.onTimelineChanged(timeline, manifest, reason)
            Log.e("Calling", "Calling")
        }
    }

    fun getCurrentDuration(): String? {
        val currentDuration = player?.currentPosition
        return stringForTime(currentDuration!!)
    }

    fun getDurationSong(): String? {
        val realDurationMillis: Long? = player?.getDuration()
        return stringForTime(realDurationMillis!!)
    }

    private fun stringForTime(timeMs: Long): String? {
        val mFormatBuilder = StringBuilder()
        val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    val listSongs = ArrayList<ModalAudioFile>()

    /*
    *  for the init for audio
    * */
    fun initSingleSong(audioFile: String,isFile: Boolean) {
        this.player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector()).apply {
            addListener(playerEventListener)
        }
        preparePlayeForSingleSong(audioFile,isFile)
    }

    fun init() {
        listSongs.addAll(SingletonInstances.listAudioFiles!!)
        this.player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector()).apply {
            addListener(playerEventListener)
        }
        preparePlayer()
    }

    private fun createMediaSourceSingleSong(
        audioFile: String,
        isFile: Boolean
    ): ConcatenatingMediaSource {

        val dataSourceFactory = DefaultDataSourceFactory(
            context, Util.getUserAgent(
                context, context.getString(
                    R.string.audio_player_service
                )
            )
        )

        var audioUrl = audioFile
        if(!audioFile.equals("")){
            val file = File(audioFile)
            if(file.exists()){

            }else{
                if(!audioFile.contains("http") || !audioFile.contains("https")){
                    audioUrl = SyncConstants.AUDIO_FILE_FULL + audioFile
                }
            }
        }

        Log.d("sadassdasddaasd","yuioyuoyuouo"+""+audioUrl )

        val concatenatingMediaSource = ConcatenatingMediaSource()
        var mediaSource: ExtractorMediaSource? = null
        mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(audioUrl))
        concatenatingMediaSource.addMediaSource(mediaSource)
        return concatenatingMediaSource
    }

    fun createMediaSource(): ConcatenatingMediaSource {

        val dataSourceFactory = DefaultDataSourceFactory(
            context, Util.getUserAgent(
                context, context.getString(
                    R.string.audio_player_service
                )
            )
        )
        val concatenatingMediaSource = ConcatenatingMediaSource()
        // TODO: load data from some real-life source
//        for (audioFile in SingeltonInstances.listAudioFiles!!) {
//
//        }
        var mediaSource: ExtractorMediaSource? = null
        val audioFile = listSongs[currentSongPlayPos]
        SingletonInstances.currentAudioFilePlay = listSongs[currentSongPlayPos]
        SingletonInstances.currentAudioFilePlayPos = currentSongPlayPos
        if (audioFile.fullAudio.toString().contains("http")) {
            mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(audioFile.fullAudio.toString()))
        } else {
            if (songType.equals(AppConstants.SongType.TRIM.value)) {
                mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(SyncConstants.AUDIO_FILE_TRIM + audioFile.trimAudio.toString()))

//                mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba-online-audio-converter.com_-1.wav"))

            } else {
                mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(SyncConstants.AUDIO_FILE_FULL + audioFile.fullAudio.toString()))

//                mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba-online-audio-converter.com_-1.wav"))

            }
        }
        concatenatingMediaSource.addMediaSource(mediaSource)

        return concatenatingMediaSource
    }

    fun repeatSongPlaySingle() {
        player?.run {
            seekToDefaultPosition()
        }
    }

    fun repeatSongPlay() {
        goToPreviousOrBeginning()
    }

    fun goToPreviousOrBeginning() {
//        player?.run {
//            if (hasPrevious()) {
//                previous()
//            } else {
//                seekToDefaultPosition()
//            }
//        }
        playPreviousSong()
    }

    fun playPreviousSong() {
        if (currentSongPlayPos > 0) {
            previousSongPlayPos = currentSongPlayPos
            currentSongPlayPos -= 1
            SingletonInstances.currentAudioFilePlay = listSongs[currentSongPlayPos]
            SingletonInstances.currentAudioFilePlayPos = currentSongPlayPos
            preparePlayer()
            exoPlayerAudioNavigator.playPreviousSound()
        } else {
            player?.run {
                seekToDefaultPosition()
            }
//            exoPlayerAudioNavigator.previousSongNotExist()
        }
    }


}