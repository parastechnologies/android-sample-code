package com.app.muselink.ui.fragments.profile

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.util.springAnimationForEqilizerView
import com.app.muselink.visualizersmooth.CircleBarVisualizerSmooth
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import com.app.muselink.widgets.visualizer.AudioPlayer
import com.app.muselink.widgets.visualizer.CircleLineVisualizer
import kotlinx.android.synthetic.main.fragment_visualizer_black.*
import java.lang.Exception


class VisualizerFragment(val modalAudioFile: ModalAudioFile) : BaseFragment(){

    private var rootView: View? = null
    var IsPlay = false
    private var circleLineVisualizer: CircleBarVisualizerSmooth? = null
    private var mAudioPlayer: AudioPlayer? = null
    private var imgPausePlay: ImageView? = null
    private var exoPlayerAudio: ExoPlayerAudio? = null

    fun playNextSong(){
        exoPlayerAudio?.playNextSong()
    }

    fun repeatSong(){
        exoPlayerAudio?.repeatSongPlay()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_visualizer_black, container, false)

        return rootView
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        mAudioPlayer = AudioPlayer()
        circleLineVisualizer = rootView?.findViewById(R.id.circleVisualizer)
//        circleLineVisualizer?.isDrawLine = true
        exoPlayerAudio = ExoPlayerAudio(requireActivity(),exoPlayerAudioNavigator, AppConstants.SongType.TRIM.value)

        val llPausePlay = rootView?.findViewById<LinearLayout>(R.id.llPausePlay)
        imgPausePlay = rootView?.findViewById<ImageView>(R.id.imgPausePlay)


        llPausePlay?.setOnClickListener {
            if (!IsPlay) {
                IsPlay = true
                imgPausePlay?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.pause
                    )
                )

                exoPlayerAudio?.startPlayAudio()
                startPlayingAudio()
//                startPlayingAudio(R.raw.sample)

            } else {
                exoPlayerAudio?.pausePlayAudio()
                IsPlay = false
                imgPausePlay?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.play_icon
                    )
                )
            }

        }

    }

    fun intializePlayer(){
        exoPlayerAudio?.initSingleSong(modalAudioFile.fullAudio.toString(),false)
    }

    val exoPlayerAudioNavigator = object :ExoPlayerAudio.ExoPlayerAudioNavigator{
        override fun onSongCompleted() {
            exoPlayerAudio?.playPreviousSong()
        }

        override fun onPlayerReady() {
            Log.e("","")
        }

        override fun onNextSongPlayed() {

        }

        override fun nextSongNotExist() {
        }

        override fun playPreviousSound() {
            Handler().postDelayed(Runnable {
                startPlayingAudio()
                exoPlayerAudio?.startPlayAudio()
            },2500)
        }

        override fun previousSongNotExist() {

        }

        override fun repeatSong() {
            Handler().postDelayed(Runnable {
                exoPlayerAudio?.startPlayAudio()
            },2500)
        }

        override fun getCurrentSongPos(value: String?) {

        }

        override fun updateProgress(value: Float) {
            if(IsPlay){
                cardInsideeProfileBottomSheet.springAnimationForEqilizerView(0.1f)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            exoPlayerAudio?.stopPlayer()
            IsPlay = false
            imgPausePlay?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.play_icon
                )
            )
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun startPlayingAudio() {
//        mAudioPlayer!!.play(requireActivity(), resId, object : AudioPlayer.AudioPlayerEvent {
//            override fun onCompleted() {
//                if (circleLineVisualizer != null) circleLineVisualizer?.hide()
//            }
//        })
        val component = exoPlayerAudio?.getAudioPlayer()?.audioComponent
        val audioSessionId = component?.audioSessionId
        if (audioSessionId != -1) circleLineVisualizer?.setPlayer(audioSessionId!!)
    }



}