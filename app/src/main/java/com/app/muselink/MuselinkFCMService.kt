package com.app.muselink

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.home.*
import java.util.*


const val TAG = "RestaurantFCMService"

class MuselinkFCMService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)

        try {
            SharedPrefs.setDeviceToken(s)
        } catch (throwable: Throwable) {
            Log.e(TAG, "Token refresh error", throwable)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        Log.d("asdadadasdasdsas", message.data.toString())

//        playNotificationSound()
//        var count = SharedPrefs.getInt(AppConstants.PREFS_NOTIFICATION_COUNT)
//        count = count + 1
//        SharedPrefs.save(AppConstants.PREFS_NOTIFICATION_COUNT,count)
//        //Send BroadCast To Update Notification Count On Profile Screen
//        val intent = Intent(AppConstants.BROADCAST_NOTIFICATION_COUNT)
//        intent.putExtra("message", "This is my message!")
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        sendNotification(message)
    }

    //    fun playNotificationSound() {
//        try {
//            val alarmSound = Uri.parse(
//                "android.resource://" +
//                        getApplicationContext().getPackageName() +
//                        "/" +
//                        R.raw.notification_ring
//            )
//            val r = RingtoneManager.getRingtone(applicationContext, alarmSound);
//            r.play();
//        } catch (e: Exception) {
//            e.printStackTrace();
//        }
//    }
    private fun sendNotification(message: RemoteMessage) {

        val body = message.notification?.body
        var title = message.notification?.title

        var type = ""
        var notificationMessages = ""
        val senderId = ""
        val senderImage = ""
        val senderName = ""
        var id = -1
        val data = message.data

        if (!data.isNullOrEmpty()) {

            type = data[NotificationConstants.NOTIFICATION_TYPE] ?: ""
            id = data["id"]?.toInt() ?: -1
            title = data[NotificationConstants.TITLE] ?: ""
            notificationMessages = data[NotificationConstants.BODY] ?: ""

            /*type = data["type"] ?: ""
            id = data["id"]?.toInt() ?: -1
            title = data["title"] ?: ""*/

            if (type.isEmpty().not()) {
                var count = SharedPrefs.getInt(AppConstants.PREFS_NOTIFICATION_COUNT)
                count += 1
                SharedPrefs.save(AppConstants.PREFS_NOTIFICATION_COUNT, count)
                val intent = Intent("notificationMessage")
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                showNotification(
                    id,
                    title,
                    notificationMessages,
                    type,
                    senderId,
                    senderName,
                    senderImage
                )
            }
        }
    }

    private fun showNotification(
        id: Int,
        title: String,
        notificationMessages: String?,
        type: String,
        senderId: String,
        senderName: String,
        senderImage: String
    ) {
        val intent = Intent(
            applicationContext,
            HomeActivity::class.java
        )
        intent.putExtra(EXTRA_NOTIFICATION_ID, id)
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, type)
        intent.putExtra(EXTRA_NOTIFICATION_TITLE, title)
        intent.putExtra(EXTRA_NOTIFICATION, true)
        intent.putExtra(EXTRA_NOTIFICATION_SENDER_ID, senderId)
        intent.putExtra(EXTRA_NOTIFICATION_SENDER_IMAGE, senderImage)
        intent.putExtra(EXTRA_NOTIFICATION_SENDER_NAME, senderName)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, AppConstants.NOTIFICATION_CHANNEL)
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder))
            .setContentTitle(title)
            .setAutoCancel(true)
//                .setContentText(body)
            .setContentText(notificationMessages)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
        var adminChannel: NotificationChannel? = null
        val random: Int = Random().nextInt(61) + 20
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationInfo(type, id)
            when {
                type.isEmpty() -> {
                    adminChannel = NotificationChannel(AppConstants.NOTIFICATION_CHANNEL, AppConstants.NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_LOW)
                    notificationManager.createNotificationChannel(adminChannel)
                    notificationManager.notify(random, notificationBuilder.build())
                }
                notification.isOrderPlacedNotification() -> {
                    adminChannel = NotificationChannel(AppConstants.NOTIFICATION_CHANNEL, AppConstants.NOTIFICATION_CHANNEL_ORDER_PLACED, NotificationManager.IMPORTANCE_LOW)
                    notificationManager.createNotificationChannel(adminChannel)
                    notificationManager.notify(random, notificationBuilder.build())
                }
                notification.isSuccessfulNotification() -> {
                    adminChannel = NotificationChannel(AppConstants.NOTIFICATION_CHANNEL, AppConstants.NOTIFICATION_CHANNEL_ORDER_SUCCESSFUL, NotificationManager.IMPORTANCE_LOW)
                    notificationManager.createNotificationChannel(adminChannel)
                    notificationManager.notify(random, notificationBuilder.build())
                }
                notification.isFailNotifications() -> {
                    adminChannel = NotificationChannel(AppConstants.NOTIFICATION_CHANNEL, AppConstants.NOTIFICATION_CHANNEL_ORDER_FAIL, NotificationManager.IMPORTANCE_LOW)
                    notificationManager.createNotificationChannel(adminChannel)
                    notificationManager.notify(random, notificationBuilder.build())
                }
            }
        }
    }
    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = 0x2DC9D7
            notificationBuilder.color = color
            R.drawable.splash_logo
        } else {
            R.drawable.splash_logo
        }
    }

}