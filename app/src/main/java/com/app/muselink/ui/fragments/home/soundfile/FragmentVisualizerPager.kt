package com.app.muselink.ui.fragments.home.soundfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.bottomsheets.description.DescriptionBottomSheet
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.util.showToast
import com.app.muselink.util.springAnimation
import com.app.muselink.util.springAnimationForEqilizerView
import com.app.muselink.util.springAnimationSingleXAxis
import com.app.muselink.visualizersmooth.CircleBarVisualizerSmooth
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import com.app.muselink.widgets.visualizer.AudioPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_visualizer_view.*
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class FragmentVisualizerPager(val dashBoardFragment: DashBoardFragment,val modalAudioFile: ModalAudioFile) : BaseFragment() {
    var rootView: View? = null
    private var circleLineVisualizer: CircleBarVisualizerSmooth? = null
    private var exoPlayerAudio: ExoPlayerAudio? = null
    private var mAudioPlayer: AudioPlayer? = null
    private var imgPausePlay: ImageView? = null
    private var isPlay = false
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    @Inject
    lateinit var repository: ApiRepository
    private var isShowDescription = false
    /**
     * Call Api for description
     * */
    private val description = requestApi.switchMap { requestApi ->
        repository.description(requestApi)
    }
    /**
     * [onCreateView]
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_visualizer_view, container, false)
        setupObservers()
        return rootView
    }
    /**
     *[onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAudioPlayer = AudioPlayer()
        exoPlayerAudio = ExoPlayerAudio(requireActivity(),exoPlayerAudioNavigator, AppConstants.SongType.TRIM.value)
        circleLineVisualizer = rootView?.findViewById(R.id.circleVisualizer)
//        circleLineVisualizer?.isDrawLine = true
        val llPausePlay = rootView?.findViewById<LinearLayout>(R.id.llPausePlay)
        imgPausePlay = rootView?.findViewById(R.id.imgPausePlay)
        btnDescription.setOnClickListener {
            isShowDescription = true
            val map: HashMap<String, String> = HashMap()
            map["audioId"] = modalAudioFile.audioId.toString()
            requestApi.value = map
        }
        llPausePlay?.setOnClickListener {
            if (!isPlay) {
                isPlay = true
                imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.pause))
                exoPlayerAudio?.startPlayAudio()
                startPlayingAudio()
            } else {
                exoPlayerAudio?.pausePlayAudio()
                isPlay = false
                imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.play_icon))
            }
        }
        initializePlayer()
    }
    /**
     * Initialize Player
     * */
    private fun initializePlayer(){
        exoPlayerAudio?.initSingleSong(modalAudioFile.fullAudio.toString(),false)
    }
    /**
     * Repeat Song
     * */
    fun repeatSong(){
        exoPlayerAudio?.repeatSongPlay()
    }

    /**
     * Pause song if song is already playing
     * */
    private fun pauseSongIfPlay(){
        if(isPlay){
            exoPlayerAudio?.pausePlayAudio()
            isPlay = false
            imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.play_icon))
        }
    }

    /**
     * [ExoPlayerAudio.ExoPlayerAudioNavigator]
     * */
    val exoPlayerAudioNavigator = object :ExoPlayerAudio.ExoPlayerAudioNavigator{
        override fun onSongCompleted() {
            isPlay = false
            imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.play_icon))
            exoPlayerAudio?.playeraudioSeekTo(0)
            exoPlayerAudio?.pausePlayAudio()
        }
        override fun onPlayerReady() {

        }
        override fun onNextSongPlayed() {

        }
        override fun nextSongNotExist() {

        }

        override fun playPreviousSound() {

        }


        override fun previousSongNotExist() {

        }
        override fun repeatSong() {

        }
        override fun getCurrentSongPos(value: String?) {

        }

        override fun updateProgress(value: Float) {
            if(isPlay){
                cardInsidee.springAnimationForEqilizerView(.1f)
            }
        }
    }

    /**
     * Visualizer
     * */
    private fun startPlayingAudio() {
        val component = exoPlayerAudio?.getAudioPlayer()?.audioComponent
        val audioSessionId = component?.audioSessionId
        if (audioSessionId != -1) circleLineVisualizer?.setPlayer(audioSessionId!!)
    }

    /**
     * Description response observer
     * */
    private fun setupObservers() {
        description.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.status.equals("200")) {
                            if(isShowDescription) {
                                isShowDescription = false
                                pauseSongIfPlay()
                                DescriptionBottomSheet(it.data.data).show((activity as AppCompatActivity).supportFragmentManager, "Description")
                            }
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    isShowDescription = false
                    hideLoader()
                    showToast(requireActivity(), it.message)
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        onStop()
    }

    override fun onStop() {
        super.onStop()
        try {
            exoPlayerAudio?.stopPlayer()
            isPlay = false
            imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.play_icon))
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}