package com.app.muselink.ui.fragments.home.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.constants.AppConstants
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.dialogfragments.MilestonesDialogFragments
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.util.showToast
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import kotlinx.android.synthetic.main.fragment_sound_pro.*
import me.tankery.lib.circularseekbar.CircularSeekBar
import java.lang.Exception

class SoundFileProFragment(val dashBoardFragment: DashBoardFragment) : BaseFragment(){
    private var rootView: View? = null

    var IsPlay = false
    private var exoPlayerAudio: ExoPlayerAudio? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_sound_pro, container, false)
        return rootView
    }

    val exoPlayerAudioNavigator = object :ExoPlayerAudio.ExoPlayerAudioNavigator{

        override fun onSongCompleted() {
            exoPlayerAudio?.playNextSong()
        }

        override fun onPlayerReady() {
            try {
                if (exoPlayerAudio?.getDurationSong()?.contains("-")!!.not()) {
                    tvSongTotalDuration.text = exoPlayerAudio?.getDurationSong()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        override fun onNextSongPlayed() {
            dashBoardFragment.updateFavButton()
            tvCurrentDuration.text = "00:00"
            exoPlayerAudio?.startPlayAudio()
        }

        override fun nextSongNotExist() {
            showToast(requireActivity(),getString(R.string.no_more_song_available))
        }

        override fun playPreviousSound() {
            dashBoardFragment.updateFavButton()
            tvCurrentDuration.text = "00:00"
            exoPlayerAudio?.startPlayAudio()
        }

        override fun previousSongNotExist() {

        }

        override fun repeatSong() {
            dashBoardFragment.updateFavButton()
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

    fun playPreviousSong(){
        exoPlayerAudio?.playPrevioussong()
    }

    fun repeatSong(){
        exoPlayerAudio?.repeatSongPlay()
    }

    fun playNextSong(){
        exoPlayerAudio?.playNextSong()
    }

    fun intilizeChnagedSong(){
        exoPlayerAudio?.intializeChnagedSonInPlayer()
    }

    fun intializePlayer(){
        exoPlayerAudio?.init()
    }

    var IsForward = false
    var durationSeekto : Long = 0

    val onCircularSeekBarChangeListener = object : CircularSeekBar.OnCircularSeekBarChangeListener{
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        circularSeekbar.progress = 0.0f
        circularSeekbar.setOnSeekBarChangeListener(onCircularSeekBarChangeListener)
        exoPlayerAudio = ExoPlayerAudio(requireActivity(),exoPlayerAudioNavigator,AppConstants.SongType.FULL.value)
        llPausePlay.setOnClickListener {
            if(SingletonInstances.listAudioFiles.isNullOrEmpty().not()) {
                if (!IsPlay) {
                    IsPlay = true
                    imgPausePlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.pause
                        )
                    )
                    exoPlayerAudio?.startPlayAudio()
                } else {
                    exoPlayerAudio?.pausePlayAudio()
                    IsPlay = false
                    circularSeekbar.progress = 0.0f
                    count = 0.0f
                    imgPausePlay.setImageDrawable(
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
        tvSoundPro?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "message",
                requireActivity().resources.getString(R.string.premium_subscription_text)
            )
            val frag = MilestonesDialogFragments()
            frag.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction().add(frag, "Milestones")
                .commit()
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