package com.app.muselink.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import kotlinx.android.synthetic.main.audio_sound_progress_fragment.*
import me.tankery.lib.circularseekbar.CircularSeekBar

class AudioSoundProgressFragment(val modalAudioFile: ModalAudioFile) : BaseFragment(){

    private var rootView: View? = null

    var IsPlay = false

    private var exoPlayerAudio: ExoPlayerAudio? = null

    var IsForward = false

    var durationSeekto : Long = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.audio_sound_progress_fragment, container, false)
        return rootView
    }

    val exoPlayerAudioNavigator = object :ExoPlayerAudio.ExoPlayerAudioNavigator{
        override fun onSongCompleted() {
            exoPlayerAudio?.playPreviousSong()
        }

        override fun onPlayerReady() {
            try {
                if (exoPlayerAudio?.getDurationSong()?.contains("-")!!.not()) {
                    tvSongTotalDuration.text = exoPlayerAudio?.getDurationSong()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        override fun onNextSongPlayed() {

        }

        override fun nextSongNotExist() {
        }

        override fun playPreviousSound() {
            tvCurrentDuration.text = "00:00"
            exoPlayerAudio?.startPlayAudio()
        }

        override fun previousSongNotExist() {

        }

        override fun repeatSong() {
            tvCurrentDuration.text = "00:00"
        }

        override fun getCurrentSongPos(value: String?) {
            tvCurrentDuration?.text = value
        }

        override fun updateProgress(value: Float) {
            if(!IsForward) {
                circularSeekbar.progress = value
            }
        }

    }

    private val onCircularSeekBarChangeListener = object : CircularSeekBar.OnCircularSeekBarChangeListener{
        override fun onProgressChanged(
            circularSeekBar: CircularSeekBar?,
            progress: Float,
            fromUser: Boolean
        ) {
            if(IsForward) {
                durationSeekto =
                    exoPlayerAudio?.getAudioPlayer()!!.duration.toLong() * progress.toInt() / 100
            }
        }

        override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            exoPlayerAudio?.getAudioPlayer()?.seekTo(durationSeekto)
            IsPlay = true
            IsForward = false
            exoPlayerAudio?.startPlayAudio()
            imgPausePlay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.pause
                )
            )
        }

        override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            IsForward = true
            exoPlayerAudio?.pausePlayAudio()
            IsPlay = false
        }

    }

    fun intializePlayer(){
        exoPlayerAudio?.initSingleSong(modalAudioFile.fullAudio.toString(),false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvSongTitle.setText(modalAudioFile.userName)
        circularSeekbar.progress = 0.0f
        circularSeekbar.setOnSeekBarChangeListener(onCircularSeekBarChangeListener)

        exoPlayerAudio = ExoPlayerAudio(requireActivity(),exoPlayerAudioNavigator, AppConstants.SongType.FULL.value)

        llPausePlay.setOnClickListener {
            if(!IsPlay){
                IsPlay = true
                imgPausePlay.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.pause))
                exoPlayerAudio?.startPlayAudio()
            }else{
                exoPlayerAudio?.pausePlayAudio()
                IsPlay = false
                circularSeekbar.progress = 0.0f
                count = 0.0f
                imgPausePlay.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.play_icon))
            }
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            exoPlayerAudio?.stopPlayer()
            IsPlay = false
            count = 0.0f
            imgPausePlay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.play_icon
                )
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    var count = 1.0f
}