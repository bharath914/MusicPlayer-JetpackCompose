package com.bharath.musicplayerforbaby.exoplayer.callbacks

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.bharath.musicplayerforbaby.exoplayer.MusicService
import com.bharath.musicplayerforbaby.other.Const

import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MusicPlayerNotificationListener(
        private val musicService: MusicService
):PlayerNotificationManager.NotificationListener {
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean,
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if (ongoing && !isForegroundService){
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext,this::class.java)
                )
                startForeground(Const.NOTIFICATION_ID,notification)
                isForegroundService  = true
            }
        }

    }
}