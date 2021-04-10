package com.android.githubapi.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.githubapi.R
import com.android.githubapi.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@Suppress("DEPRECATION")
class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val pendingIntent = PendingIntent.getActivity(
            baseContext,
            NOTIFY_TAG,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val notification = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(
                getString(R.string.app_name) + " - " + remoteMessage.notification!!.title
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(remoteMessage.notification!!.body)
            )
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 2000))
            .setContentText(remoteMessage.notification!!.body)
            .setContentIntent(pendingIntent).build()
        val context = baseContext
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFY_TAG, notification)
    }

    companion object {
        private const val NOTIFY_TAG = 123
    }
}