package com.bharath.musicplayerforbaby.exoplayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.bharath.musicplayerforbaby.other.Const.MEDIA_ROOT_ID
import com.bharath.musicplayerforbaby.other.Const.NETWORK_ERROR
import com.bharath.musicplayerforbaby.exoplayer.callbacks.MusicPlaybackPreparer
import com.bharath.musicplayerforbaby.exoplayer.callbacks.MusicPlayerEventListener
import com.bharath.musicplayerforbaby.exoplayer.callbacks.MusicPlayerNotificationListener

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoplayer: SimpleExoPlayer

    @Inject
    lateinit var localMusicSource: LocalMusicSource

    lateinit var musicNotificationManager: MusicNotificationManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var musicPlayerEventListener : MusicPlayerEventListener
    var isForegroundService= false
    private var isPlayerInitialized = false
    private var curPlayingSong:MediaMetadataCompat ? = null

    companion object{
        var currentSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            localMusicSource.fetchMediaData()
        }

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this),

        ){

            currentSongDuration = exoplayer.duration
        }
        val musicPlaybackPreparer = MusicPlaybackPreparer(
            localMusicSource
        ){
            curPlayingSong = it
            preparePlayer(
                localMusicSource.songs,
                it,
                true
            )
        }



        mediaSessionConnector = MediaSessionConnector(mediaSession )

        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setPlayer(exoplayer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())

        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoplayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoplayer)

    }
    private inner class MusicQueueNavigator:TimelineQueueNavigator(mediaSession){
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return localMusicSource.songs[windowIndex].description
        }

    }

    private fun preparePlayer(
        songs:List<MediaMetadataCompat>,
        itemToPlay :MediaMetadataCompat?,
        playNow :Boolean
    ){
        val currSongIndex = if (curPlayingSong == null)0 else songs.indexOf(itemToPlay)
        exoplayer.prepare(  localMusicSource.asMediaSource(dataSourceFactory))
        exoplayer.seekTo(currSongIndex,0L)
        exoplayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoplayer.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoplayer.removeListener(musicPlayerEventListener)
        exoplayer.release()
    }








    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot? {

        return BrowserRoot(MEDIA_ROOT_ID,null)


    }









    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
    ) {
        when(parentId){
            MEDIA_ROOT_ID -> {
                val resultSent = localMusicSource.whenReady { isInitialized ->
                    if (isInitialized){
                        result.sendResult(localMusicSource.asMediaItems())
                        if (!isPlayerInitialized && localMusicSource.songs.isNotEmpty()){
                            preparePlayer(localMusicSource.songs ,localMusicSource.songs[0],false)
                            isPlayerInitialized = true
                        }
                    }
                    else{
                        mediaSession.sendSessionEvent(NETWORK_ERROR,null)
                        result.sendResult(null)
                    }

                }
                if (!resultSent){
                    result.detach()
                }
            }
        }


    }
}