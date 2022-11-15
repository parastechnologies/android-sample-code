package com.app.muselink.ui.fragments.home.viewpager.soundfile

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.constants.AppConstants
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.util.showToast
import com.app.muselink.visualizersmooth.CircleBarVisualizerSmooth
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import com.app.muselink.widgets.visualizer.AudioPlayer
import com.app.muselink.widgets.visualizer.CircleLineVisualizer
import kotlinx.android.synthetic.main.fragment_visualizer.*
import java.lang.Exception

class VislualizerFragmentHome(val dashBoardFragment :DashBoardFragment) : BaseFragment() {

    var IsPlay = false
    private var rootView: View? = null
    private var circleLineVisualizer: CircleBarVisualizerSmooth? = null
    private var exoPlayerAudio: ExoPlayerAudio? = null
    private var mAudioPlayer: AudioPlayer? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_visualizer, container, false)
        return rootView
    }
    fun playNextSong(){
        exoPlayerAudio?.playNextSong()
    }
    fun repeatSong(){
        exoPlayerAudio?.repeatSongPlay()
    }
    val exoPlayerAudioNavigator = object :ExoPlayerAudio.ExoPlayerAudioNavigator{
        override fun onSongCompleted() {
            exoPlayerAudio?.playNextSong()
        }
        override fun onPlayerReady() {
            dashBoardFragment.updateFavButton()
        }
        override fun onNextSongPlayed() {
            dashBoardFragment.updateFavButton()
            Handler().postDelayed({
                startPlayingAudio()
                exoPlayerAudio?.startPlayAudio()
            },3000)
        }
        override fun nextSongNotExist() {
            showToast(requireActivity(),getString(R.string.no_more_song_available))
        }
        override fun playPreviousSound() {
            dashBoardFragment.updateFavButton()
            Handler().postDelayed(Runnable {
                startPlayingAudio()
                exoPlayerAudio?.startPlayAudio()
            },3000)
        }
        override fun previousSongNotExist() {
        }
        override fun repeatSong() {
            dashBoardFragment.updateFavButton()
            Handler().postDelayed({
                exoPlayerAudio?.startPlayAudio()
            },2500)
        }
        override fun getCurrentSongPos(value: String?) {
        }
        override fun updateProgress(value: Float) {
        }
    }

    override fun onPause() {
        super.onPause()
    }

    fun intializePlayer(){
        exoPlayerAudio?.init()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAudioPlayer = AudioPlayer()
        exoPlayerAudio = ExoPlayerAudio(requireActivity(),exoPlayerAudioNavigator,AppConstants.SongType.TRIM.value)
        circleLineVisualizer = view.findViewById(R.id.circleVisualizer)
//        circleLineVisualizer?.isDrawLine = true
        setListeners()
    }

    private fun setListeners() {

        llPausePlay.setOnClickListener {
            if(SingletonInstances.listAudioFiles.isNullOrEmpty().not()){
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
            }else{
                showToast(requireActivity(),getString(R.string.no_audios_found))
            }
        }

    }

    fun pauseSongIfPlay(){
        if(IsPlay){
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

    override fun onStop() {
        super.onStop()
        try {
            exoPlayerAudio?.stopPlayer()
            IsPlay = false
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