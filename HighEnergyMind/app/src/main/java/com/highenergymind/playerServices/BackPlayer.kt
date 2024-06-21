package com.highenergymind.playerServices

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer


/**
 * Created by developer on 13/05/24
 */
object BackPlayer {
     var exoPlayer: ExoPlayer? = null
    fun initializeBackground(context: Context, backUrl: String) {
        exoPlayer?.release()
        exoPlayer = ExoPlayer.Builder(context).build()
        exoPlayer?.clearMediaItems()
        exoPlayer?.addMediaItem(
            MediaItem.fromUri(backUrl)
        )
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = false
    }
}