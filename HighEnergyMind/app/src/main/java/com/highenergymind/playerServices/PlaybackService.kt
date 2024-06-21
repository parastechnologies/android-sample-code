package com.highenergymind.playerServices

import android.content.Intent
import androidx.media3.common.ForwardingPlayer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService


/**
 * Created by developer on 14/03/24
 */
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null


    // Create your player and media session in the onCreate lifecycle event
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        val forwardingPlayer = object : ForwardingPlayer(player) {


        }


        mediaSession =
            MediaSession.Builder(this, forwardingPlayer)
                .setCallback(object : MediaSession.Callback {

                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ): MediaSession.ConnectionResult {

                        return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                            .setAvailablePlayerCommands(
                                MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                                    .removeAll(
                                    )
                                    .build()
                            )

                            .build()
                    }
                }
                ).build()

    }


    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}