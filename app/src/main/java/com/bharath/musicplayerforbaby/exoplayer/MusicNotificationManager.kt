package com.bharath.musicplayerforbaby.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.other.Const.NOTIFICATION_CHANNEL_ID
import com.bharath.musicplayerforbaby.other.Const.NOTIFICATION_ID

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener


/*
This is the main class for music notifications this
will be used to change the music notification style
customize it on your own
 */
class MusicNotificationManager (
    private val context : Context,
    sessionToken:MediaSessionCompat.Token,
    notificationListener: NotificationListener,
    private val newSongCallback: () ->Unit
        ){

    val mediaController = MediaControllerCompat(context, sessionToken)

    private val notificationManager:PlayerNotificationManager
    init {
        notificationManager=PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,




        ).setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.channel_description)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setNotificationListener(notificationListener)


            .build().apply {
                setSmallIcon(R.drawable.ic_music)
                setMediaSessionToken(sessionToken)
                setUseNextAction(true)
                setUsePreviousAction(true)
                setUseFastForwardAction(false)
                setUseStopAction(true)
                setUseRewindAction(false)
            }
    }


    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }







    private inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ):PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
            newSongCallback()
            return  mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
           return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback,
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                            callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) =Unit

                })
            return null
        }

    }

}