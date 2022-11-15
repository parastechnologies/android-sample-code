package com.app.muselink.ui.fragments.home.soundfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_viewpager_soundfiles.*

@AndroidEntryPoint
class SoundFileViewPagerFragment (val dashBoardFragment: DashBoardFragment) : BaseFragment() {

    var rootView: View? = null
    var soundFileViewPagerAdapter : SoundFileViewPagerAdapter? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_viewpager_soundfiles, container, false)
        return rootView
    }
    var listAudioFiles : ArrayList<ModalAudioFile>? = null
    var currentAudioSong : ModalAudioFile? = null
    var currentPos : Int? = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundFileViewPagerAdapter = SoundFileViewPagerAdapter(childFragmentManager)
        listAudioFiles = SingletonInstances.listAudioFiles
        if(listAudioFiles.isNullOrEmpty().not()){
            for(i in 0 until listAudioFiles!!.size){
                soundFileViewPagerAdapter?.add(FragmentVisualizerPager(dashBoardFragment,listAudioFiles!![i]))
            }
        }
        viewpagerSoundFiles.adapter = soundFileViewPagerAdapter
        viewpagerSoundFiles?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                currentPos = position
                if(listAudioFiles.isNullOrEmpty().not()) {
                    currentAudioSong = listAudioFiles!![position]
                    SingletonInstances.currentAudioFilePlay = currentAudioSong
                    ExoPlayerAudio.previousSongPlayPos =  ExoPlayerAudio.currentSongPlayPos
                    SingletonInstances.currentAudioFilePlayPos = currentPos
                    ExoPlayerAudio.currentSongPlayPos = currentPos!!
                    dashBoardFragment.updateFavButton()
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
    fun playNextSong(){
        gotoNext()
    }
    fun playPreviousSong(){
        gotoPrevious()
    }
    fun repeatSong(){
        val fragment = soundFileViewPagerAdapter?.getFragmentAccordingPos(currentPos!!)
        if(fragment!=null && fragment is FragmentVisualizerPager) {
            fragment.repeatSong()
        }
    }
    fun gotoParticularSelectedSong(){
        viewpagerSoundFiles.currentItem =  SingletonInstances.currentAudioFilePlayPos!!
    }
    fun gotoNext(){
        viewpagerSoundFiles?.currentItem = viewpagerSoundFiles.currentItem + 1
    }
    fun gotoPrevious(){
        viewpagerSoundFiles?.currentItem = viewpagerSoundFiles.currentItem - 1
    }
}