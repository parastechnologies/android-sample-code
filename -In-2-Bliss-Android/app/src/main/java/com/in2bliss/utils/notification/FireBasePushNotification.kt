package com.in2bliss.utils.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.utils.constants.AppConstant
import java.util.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FireBasePushNotification : FirebaseMessagingService() {

    private var notificationId: Int? = null
    private var data: MutableMap<String, String>? = null

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val TYPE = "type"
        private const val ID = "id"
        private const val DATA_ID = "dataID"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        data = message.data
        val pendingIntent = createPendingIntent()
        val notification = createNotificationBuilder(
            title = getMessageData(
                type = TITLE
            ),
            description = getMessageData(
                type = MESSAGE
            ),
            pendingIntent = pendingIntent
        )
        val notificationManager = createNotificationManager()
        notificationManager.notify(
            notificationId ?: 0,
            notification
        )
    }

    private fun createPendingIntent(): PendingIntent? {
        val bundle = NotificationModel(
            id = getMessageData(type = ID),
            type = getMessageData(type = TYPE),
            title = getMessageData(type = TITLE),
            message = getMessageData(type = MESSAGE),
            dataId = getMessageData(type = DATA_ID)
        )
        val pendingIntent = when (getMessageData(type = TYPE)) {
            "3", "4", "0" -> {
                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                intent.putExtra(AppConstant.NOTIFICATION_TYPE, Gson().toJson(bundle))
                PendingIntent.getActivity(
                    this, Random().nextInt(1000), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            else -> null
        }
        return pendingIntent
    }

    /**
     * Creating notification manager
     * */
    private fun createNotificationManager(): NotificationManager {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            this.getString(R.string.app_name),
            this.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)
        return notificationManager
    }

    /**
     * Creating notification builder
     * @param title of the notification
     * @param description of the notification
     * @param pendingIntent for the navigation
     * */
    private fun createNotificationBuilder(
        title: String,
        description: String,
        pendingIntent: PendingIntent?
    ): Notification {

        notificationId = (1..1000).random()

        val notification = NotificationCompat.Builder(this, this.getString(R.string.app_name))
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description)
            )
            .setSmallIcon(R.mipmap.ic_app_logo)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (pendingIntent != null) {
            notification.contentIntent = pendingIntent
        }
        return notification
    }

    /**
     * Getting the message data
     * @param type key which value you want to access
     * */
    private fun getMessageData(
        type: String
    ): String {
        return data?.get(type).orEmpty()
    }
}