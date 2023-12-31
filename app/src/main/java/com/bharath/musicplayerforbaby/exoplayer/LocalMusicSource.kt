    package com.bharath.musicplayerforbaby.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import androidx.core.net.toUri
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.data.MusicRepository
import com.bharath.musicplayerforbaby.data.firebase.FireBaseMusicSource
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


/*
This is actually not needed for local music
but in future scope like updating to cloud storage this will be very helpful
 */
class LocalMusicSource @Inject constructor(
    private val musicDataBase: MusicRepository,
    private val fireBaseMusicSource: FireBaseMusicSource
) {

    var allSongs = listOf<DetailSong>()
    var songs = emptyList<MediaMetadataCompat>()

    /*
    We fetch all the songs in the device and map them as a mediametadatacompat objects

     */
    suspend fun fetchMediaData() = withContext(Dispatchers.Main) {
        state = State.STATE_INITIALIZING
        allSongs = fireBaseMusicSource.getMusicCollection()

        songs = allSongs.map { song ->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_ARTIST, song.artist)
                .putString(METADATA_KEY_TITLE, song.title)
                .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                .putString(METADATA_KEY_MEDIA_ID, song.mediaId)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.artist)
                .putString(METADATA_KEY_ALBUM_ARTIST, song.albumartist)


                .build()
        }
        state = State.STATE_INITIALIZED

    }

    /*
    This will create  a continuous data source by concatenating each and every mediaid
     */
    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            )

            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    /*
    This method is also optional if you have an extension funtion and provided it in the
    mediametadata extension class
    you can use that as well
     */
    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(song.description.mediaId)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setIconUri(song.description.iconUri)
            .setMediaUri(song.description.mediaUri)

            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)

    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()
    private var state: State = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITIALIZED || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.STATE_INITIALIZED)

                    }
                }
            } else {
                field = value
            }
        }


    /*
    Simply it returns true if only the songs are fetched from the device
    if this function is not used in then our app will be buggy and crashes when we launch it in lower end devices

     */
    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == State.STATE_CREATED || state == State.STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(state == State.STATE_INITIALIZED)
            return true
        }
    }
}

/*
Sealed class can be used here
 */
enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}