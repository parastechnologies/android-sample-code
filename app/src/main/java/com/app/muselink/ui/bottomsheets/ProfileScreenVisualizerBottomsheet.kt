package com.app.muselink.ui.bottomsheets

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.util.springAnimationForEqilizerView
import com.app.muselink.visualizersmooth.CircleBarVisualizerSmooth
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import com.app.muselink.widgets.visualizer.AudioPlayer
import com.app.muselink.widgets.visualizer.CircleLineVisualizer
import kotlinx.android.synthetic.main.profile_visualizer_bottom_sheet.*
import soup.neumorphism.NeumorphButton
import soup.neumorphism.NeumorphCardView
import java.lang.Exception

class ProfileScreenVisualizerBottomsheet (context: Context,val profileScreenVisualizerBottomsheetNavigator: ProfileScreenVisualizerBottomsheetNavigator,val modalAudioFile: ModalAudioFile) :  BottomSheetDialogFragment(){


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    var IsPlay = false

    private var rootView: View? = null
    private var circleLineVisualizer: CircleBarVisualizerSmooth? = null
    private var mAudioPlayer: AudioPlayer? = null
    private var imgPausePlay: ImageView? = null

    interface ProfileScreenVisualizerBottomsheetNavigator{
        fun onClickRemove()
        fun onClickCopy()
        fun onClickShareTo()
        fun onClickNotificationOnOff()
    }

    private var exoPlayerAudio: ExoPlayerAudio? = null

    val exoPlayerAudioNavigator = object :ExoPlayerAudio.ExoPlayerAudioNavigator{
        override fun onSongCompleted() {
            exoPlayerAudio?.playPreviousSong()
        }

        override fun onPlayerReady() {
            Log.e("","")
        }

        override fun onNextSongPlayed() {}

        override fun nextSongNotExist() {}

        override fun playPreviousSound() {
            Handler().postDelayed({
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
                cardInsideeProfileVisualizer.springAnimationForEqilizerView(.1f)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.profile_visualizer_bottom_sheet, container, false)
        mAudioPlayer = AudioPlayer()
        circleLineVisualizer = rootView?.findViewById(R.id.circleVisualizer)
//        circleLineVisualizer?.isDrawLine = true
        exoPlayerAudio = ExoPlayerAudio(requireActivity(),exoPlayerAudioNavigator, AppConstants.SongType.TRIM.value)


        val btnClose = rootView?.findViewById<NeumorphButton>(R.id.btnClose)

        val cardRemove = rootView?.findViewById<NeumorphCardView>(R.id.cardRemove)
        val cardCopyLink = rootView?.findViewById<NeumorphCardView>(R.id.cardCopyLink)
        val cardShareTo = rootView?.findViewById<NeumorphCardView>(R.id.cardShareTo)
        val cardNotificationOnOff = rootView?.findViewById<NeumorphCardView>(R.id.cardNotificationOnOff)
        val tvNotificationOnOff = rootView?.findViewById<TextView>(R.id.tvNotificationOnOff)
        val tvCaptionNotification = rootView?.findViewById<TextView>(R.id.tvCaptionNotification)


        if(modalAudioFile.Notification_Status.isNullOrEmpty().not()){
            if(modalAudioFile.Notification_Status.equals("1")){
                tvNotificationOnOff?.setText(getString(R.string.notification_on))
                tvCaptionNotification?.setText(getString(R.string.turn_off_notifications_for_this_soundfile))
            }else{
                tvNotificationOnOff?.setText(getString(R.string.notification_off))
                tvCaptionNotification?.setText(getString(R.string.turn_on_notifications_for_this_soundfile))
            }
        }else{
            tvNotificationOnOff?.setText(getString(R.string.notification_off))
            tvCaptionNotification?.setText(getString(R.string.turn_on_notifications_for_this_soundfile))
        }

        cardRemove?.setOnClickListener {
            profileScreenVisualizerBottomsheetNavigator?.onClickRemove()
            dismiss()
        }

        cardCopyLink?.setOnClickListener {
            dismiss()
            profileScreenVisualizerBottomsheetNavigator.onClickCopy()
        }

        cardShareTo?.setOnClickListener {
            profileScreenVisualizerBottomsheetNavigator.onClickShareTo()
        }

        cardNotificationOnOff?.setOnClickListener {
            profileScreenVisualizerBottomsheetNavigator?.onClickNotificationOnOff()
            dismiss()
        }

        intializePlayer()

        val llPausePlay = rootView?.findViewById<LinearLayout>(R.id.llPausePlay)
        imgPausePlay = rootView?.findViewById<ImageView>(R.id.imgPausePlay)

        btnClose?.setOnClickListener {
            dismiss()
        }

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


//        val containerVisualizer = contentView.findViewById
////        requireFragmentManager().beginTransaction().add(<FrameLayout>(R.id.containerVisualizer)
//containerVisualizer.id,VislualizerFragment(),"Visualizer").commit()

        return rootView

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

    fun intializePlayer(){
        exoPlayerAudio?.initSingleSong(modalAudioFile.fullAudio.toString(),false)
    }

    private fun stopPlayingAudio() {
        if (mAudioPlayer != null) mAudioPlayer!!.stop()
        if (circleLineVisualizer != null) circleLineVisualizer?.release()
    }

    private fun startPlayingAudio() {
        val component = exoPlayerAudio?.getAudioPlayer()?.audioComponent
        val audioSessionId = component?.audioSessionId
        if (audioSessionId != -1) circleLineVisualizer?.setPlayer(audioSessionId!!)
    }

}