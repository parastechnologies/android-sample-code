package com.highenergymind.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.highenergymind.R
import com.highenergymind.view.activity.notification.NotificationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseNotification : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    /**
     * every new Notification from Firebase will come there first
     * */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            makeNotification(HashMap(message.data))
        }
    }


    /**
     * Notification Builder method->
     */

    private fun makeNotification(data: HashMap<String, String>) {
        try {
            val notificationId = resources.getString(R.string.app_name)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    notificationId,
                    resources.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
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
            val intent = Intent(this, NotificationActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                Random.nextInt(1000),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            /** Notification Builder*/
            val notification = NotificationCompat.Builder(this, notificationId)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(
                    Html.fromHtml(
                        data["title"] ?: "",
                        Html.FROM_HTML_MODE_LEGACY
                    )
                ).setContentText(
                    Html.fromHtml(
                        data["message"] ?: "",
                        Html.FROM_HTML_MODE_LEGACY
                    )
                ).also {
                    if (!data["backgroundAffirmationImg"].isNullOrEmpty()) {
                        val futureTarget = Glide.with(this).asBitmap()
                            .load(data["backgroundAffirmationImg"]).submit()
                        val bitmap = futureTarget.get()
                        it.setLargeIcon(bitmap)
                    }
                }.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            Html.fromHtml(
                                data["message"] ?: "",
                                Html.FROM_HTML_MODE_LEGACY
                            )
                        )
                ).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).also {
                    it.setContentIntent(pendingIntent)
                }.build()
            manager.notify(Random.nextInt(1000), notification)

        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                Log.i("my_log_tag", "makeNotification: " + e.message.toString())
            }
        }
    }
}