package com.in2bliss.ui.activity.home.myAffirmation

import android.content.Context
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.backgroundMusicPlayer.BackgroundMusicPlayerImpl
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyAffirmationViewModel @Inject constructor() : BaseViewModel() {

    var player: BackgroundMusicPlayerInterface? = null
    var position = -1
    var isFavourite: Boolean = true
    var isDelete = false

    val adapter by lazy {
        RecordingsAdapter(player = player)
    }

    val adapterWritten by lazy {
        WrittenAdapter()
    }

    /**
     * Initializing the player
     * @param context
     * */
    fun initializePLayer(
        context: Context
    ) {
        if (player == null) {
            player = getPlayerInstance()
        }
        player?.initializePlayer(
            context = context
        )
    }

    private fun getPlayerInstance(): BackgroundMusicPlayerInterface {
        return BackgroundMusicPlayerImpl()
    }

    override fun retryApiRequest(apiName: String) {
    }

    override fun onCleared() {
        super.onCleared()
        player?.releasePlayer()
    }
}