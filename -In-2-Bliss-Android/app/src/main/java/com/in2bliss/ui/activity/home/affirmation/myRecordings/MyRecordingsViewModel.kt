package com.in2bliss.ui.activity.home.affirmation.myRecordings

import android.content.Context
import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.backgroundMusicPlayer.BackgroundMusicPlayerImpl
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet.MusicCategoryBottomSheet
import com.in2bliss.ui.activity.home.myAffirmation.RecordingsAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyRecordingsViewModel @Inject constructor() : BaseViewModel() {

    val dateAndTime = ObservableField("")
    val title = ObservableField("")
    val startTime = ObservableField("")
    val endTime = ObservableField("")

    var createAffirmation: CreateAffirmation? = null
    var player: BackgroundMusicPlayerInterface? = null
    var musicCategoriesBottomSheet: MusicCategoryBottomSheet? = null

    val adapter by lazy {
        RecordingsAdapter(
            player = player
        )
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