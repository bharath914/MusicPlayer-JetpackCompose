package com.bharath.musicplayerforbaby.exoplayer.callbacks

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bharath.musicplayerforbaby.exoplayer.LocalMusicSource
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector


/*
This class will prepare the music playback
using the media id of the song
This class will take care of the playback like when the app is launched which song should be on the notification bar etc
it will return null when the app launched
and then it will searches song by using mediaId
 */
class MusicPlaybackPreparer(
    private val localMusicSource: LocalMusicSource,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit,
) : MediaSessionConnector.PlaybackPreparer {

    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?,
    ): Boolean = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }


    override fun onPrepare(playWhenReady: Boolean) = Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        localMusicSource.whenReady {


            val itemToPlay = localMusicSource.songs.find {

                mediaId == it.description.mediaId

            }
            playerPrepared(itemToPlay)
        }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}