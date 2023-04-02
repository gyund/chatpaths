package com.gy.chatpaths.aac.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "myFBMS"

    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it, remoteMessage.data)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(
        notification: RemoteMessage.Notification,
        data: MutableMap<String, String>,
    ) {
        val channelId =
            notification.channelId ?: getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(R.drawable.ic_chatpath_logo_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)

        setIntent(data, notificationBuilder)

        // Send the notification
        val notificationManager = updateNotificationChannel(
            channelId,
            notificationBuilder,
            getColorOverride(data),
        )
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getColorOverride(data: MutableMap<String, String>): Int? {
        return when (data["color"]) {
            "blue" -> getColor(android.R.color.holo_blue_dark)
            "yellow" -> getColor(R.color.primaryDarkColor)
            "red" -> getColor(android.R.color.holo_red_dark)
            "green" -> getColor(android.R.color.holo_green_dark)
            "orange" -> getColor(android.R.color.holo_orange_dark)
            "purple" -> getColor(android.R.color.holo_purple)
            else -> null
        }
    }

    /**
     * Based on data in the message, set the appropriate intent
     */
    private fun setIntent(
        data: MutableMap<String, String>,
        notificationBuilder: NotificationCompat.Builder,
    ) {
        val uri = getUriFromMessageData(data)
        if (null != uri) {
            createIntentForWeb(uri)?.apply {
                notificationBuilder.setContentIntent(this)
            }
        } else {
            notificationBuilder.setContentIntent(createIntentForMainActivity())
        }
    }

    private fun getUriFromMessageData(data: MutableMap<String, String>): Uri? {
        return try {
            data["uri"]?.let {
                Uri.parse(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Create an intent that directs the user to the [MainActivity]
     */
    private fun createIntentForMainActivity(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT,
        )
    }

    /**
     * Set the category and type of notification based on the channelId
     */
    private fun updateNotificationChannel(
        channelId: String,
        notificationBuilder: NotificationCompat.Builder,
        @ColorInt colorOverride: Int?,
    ): NotificationManager {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelName = when (channelId) {
            getString(R.string.updates_notification_channel_id) -> {
                notificationBuilder.setCategory(NotificationCompat.CATEGORY_SOCIAL)
                notificationBuilder.color = colorOverride ?: getColor(R.color.primaryDarkColor)
                getString(R.string.updates_notification_channel_name)
            }
            getString(R.string.promo_notification_channel_id) -> {
                notificationBuilder.setCategory(NotificationCompat.CATEGORY_PROMO)
                notificationBuilder.color =
                    colorOverride ?: getColor(android.R.color.holo_green_dark)
                getString(R.string.promo_notification_channel_name)
            }
            else -> {
                notificationBuilder.color = colorOverride ?: getColor(android.R.color.black)
                getString(R.string.default_notification_channel_name)
            }
        }
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }

    private fun createIntentForWeb(uri: Uri): PendingIntent? {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        return if (intent.resolveActivity(packageManager) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT,
            )
        } else {
            null
        }
    }
}
