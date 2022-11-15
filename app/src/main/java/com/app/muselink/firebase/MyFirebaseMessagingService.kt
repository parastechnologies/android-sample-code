package com.app.muselink.firebase

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.app.muselink.R
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.home.HomeActivity

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationChannelId = "10001"
    private val defaultNotificationChannelId = "default"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        try {
            createNotification(message.data)
        } catch (e: Exception) {
        }
    }
    private fun createNotification(remoteMessage: MutableMap<String, String>) {
        val notificationType = remoteMessage["notification_type"]
        val intent = Intent(this, HomeActivity::class.java)
        val title = remoteMessage["title"]
        val message = remoteMessage["message"]
        if (notificationType == "Profile_Match" || notificationType == "AudioMatch") {
            val type = remoteMessage["type"]
            val fromId = remoteMessage["From_Id"]
            val badge = remoteMessage["badge"]
            val fromProfileImage = remoteMessage["From_Profile_Image"]
            val fromUserName = remoteMessage["From_User_Name"]
            val bundle = bundleOf(
                AppConstants.TITLE to "title",
                "message" to message,
                AppConstants.TYPE to type,
                AppConstants.receiverId to fromId,
                "badge" to badge,
                "fromProfileImage" to fromProfileImage,
                AppConstants.receiverName to fromUserName
            )
            if (checkAppInBackGround()) {
                intent.putExtras(bundle)
            } else {
                intent.putExtras(bundle)
                sendAction(bundle)
                return
            }
        }
        val contentIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val mRemoteViews = RemoteViews(this.packageName, R.layout.layout_notification_view)
        mRemoteViews.setTextViewText(R.id.message, message)
        if (isDarkTheme(this)) {
            mRemoteViews.setTextColor(R.id.message, ContextCompat.getColor(this, R.color.white))
        }
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder =
            NotificationCompat.Builder(applicationContext, defaultNotificationChannelId)
        mBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        mBuilder.setCustomContentView(mRemoteViews)
        mBuilder.setSmallIcon(R.drawable.splash_logo)
        mBuilder.color = Color.parseColor("#002547")
        mBuilder.setContentTitle(title)
        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentText("")
        mBuilder.setAutoCancel(true)
        mBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(
                    notificationChannelId,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
            mBuilder.setChannelId(notificationChannelId)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())

    }

    private fun isDarkTheme(activity: Context): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun sendAction(bundle: Bundle) {
        val intent = Intent("custom-event-name")
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun checkAppInBackGround(): Boolean {
        val myProcess: ActivityManager.RunningAppProcessInfo =
            ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        return myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }

}