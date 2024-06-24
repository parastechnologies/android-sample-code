package com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet

import android.content.Context
import androidx.databinding.ObservableField
import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.backgroundMusicPlayer.BackgroundMusicPlayerImpl
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicCateGoryViewModel @Inject constructor(
    private val requestManager: RequestManager
) : BaseViewModel() {

    val search = ObservableField("")
    var isMusicCategory = false
    var musicCategories: MusicCategories? = null
    var musicList: MusicList? = null
    var selectedMusic: MusicList.Data? = null
    var musicPlayer: BackgroundMusicPlayerInterface? = null

    val categoryAdapter by lazy {
        MusicCategoryAdapter(
            requestManager = requestManager
        )
    }

    val musicListAdapter by lazy {
        MusicListAdapter(
            requestManager = requestManager
        )
    }

    /**
     * Initializing music player
     * */
    fun initializeMusicPlayer(
        context: Context
    ) {
        if (musicPlayer == null) {
            musicPlayer = getMusicPlayerInstance()
        }
        musicPlayer?.initializePlayer(
            context = context
        )
    }

    private fun getMusicPlayerInstance(): BackgroundMusicPlayerInterface {
        return BackgroundMusicPlayerImpl()
    }

    override fun retryApiRequest(apiName: String) {
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayer?.releasePlayer()
    }
}