package com.highenergymind.view.activity.trackplay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.highenergymind.R
import com.highenergymind.base.BaseViewModel
import com.highenergymind.data.CustomAffirmationModel
import com.highenergymind.data.TrackOb
import com.highenergymind.playerServices.AffirmPlayer
import com.highenergymind.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


/**
 * Created by developer on 18/04/24
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val sharedPrefs: SharedPrefs
) :
    BaseViewModel(context) {
    lateinit var customAffirmationModel: CustomAffirmationModel

    var trackNotificationId = 3200
    var trackDetail: TrackOb? = null

    fun initializePlayer() {

        AffirmPlayer.initializeAffirmations(
            getViewContext(),
            trackDetail?.affirmationList!!,
            customAffirmationModel
        )
        viewModelScope.launch(Dispatchers.IO) {
            createNotification(true)
        }
    }

    suspend fun createNotification(isPlay: Boolean) {
        val notificationId = getViewContext().resources.getString(R.string.notification)
        val manager =
            getViewContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                notificationId,
                getViewContext().resources.getString(R.string.notification),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION
                    ),
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
                manager.createNotificationChannel(this)
            }
        }
        val intent = Intent(getViewContext(), PlayTrackActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            getViewContext(),
            Random.nextInt(1000),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val inBroad = Intent(getViewContext().getString(R.string.play_pause_intent_filter))
        val broadIntent = PendingIntent.getBroadcast(
            getViewContext(),
            Random.nextInt(1000),
            inBroad,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(getViewContext(), notificationId)
            .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(
                getViewContext().getString(R.string.playing) +
                        trackDetail?.trackTitle
            ).setContentText(
                trackDetail?.trackDesc
            ).also {

                val futureTarget = Glide.with(getViewContext()).asBitmap()
                    .load(trackDetail?.trackThumbnail).submit()
                val bitmap = futureTarget.get()
                it.setLargeIcon(bitmap)
                it.addAction(
                    R.drawable.hem_picture_logo,
                    if (isPlay) getViewContext().getString(R.string.play) else getViewContext().getString(
                        R.string.pause
                    ),
                    broadIntent
                )
            }.setPriority(NotificationCompat.PRIORITY_LOW).setAutoCancel(false).setOngoing(true)
            .also {
                it.setContentIntent(pendingIntent)
            }.build()
        manager.notify(trackNotificationId, notification)
    }

    fun closeNotification() {
        val manager =
            getViewContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(trackNotificationId)

    }


    override fun retry(type: String) {

    }
}