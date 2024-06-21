package com.mindbyromanzanoni.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.view.activity.chatList.UserChatListActivity
import com.mindbyromanzanoni.view.activity.dashboard.DashboardActivity
import com.mindbyromanzanoni.view.activity.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage.data)
        }
    }
    private fun sendNotification(remoteMessage: MutableMap<String, String>) {
        val title = remoteMessage["title"]
        val message = if(remoteMessage.containsKey("message")) remoteMessage["message"] else remoteMessage["body"]
        val intent: Intent?
        if (sharedPrefs.getUserLogin()) {
            if (remoteMessage["type"]=="4"){
                intent = Intent(this, UserChatListActivity::class.java)
                intent.putExtra(AppConstants.RECEIVER_USER_ID,remoteMessage["senderId"]?.toInt()?:0)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val broadcastIntent=Intent("REFRESH")
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
            }else{
                intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent: PendingIntent = getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.splash_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}









