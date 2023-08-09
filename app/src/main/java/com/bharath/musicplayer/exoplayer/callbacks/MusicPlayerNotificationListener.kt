package com.bharath.musicplayer.exoplayer.callbacks

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.bharath.musicplayer.exoplayer.MusicService
import com.bharath.musicplayer.other.Const

import com.google.android.exoplayer2.ui.PlayerNotificationManager

/*
This class basically will have the two methods onNotification cancelled and onNotificationPosted
these two methods will help us in maintaining the notification properly
eg : Notification will be cleared when the user swipe the notification in notification bar (if not song is playing)

 */
class MusicPlayerNotificationListener(
    private val musicService: MusicService,
) : PlayerNotificationManager.NotificationListener {
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
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(Const.NOTIFICATION_ID, notification)
                isForegroundService = true
            }
        }

    }
}