package com.bharath.musicplayerforbaby.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.bharath.musicplayerforbaby.data.Song

fun    MediaMetadataCompat.toSong(): Song {
    return description.let {
        Song(
            mediaId = it.mediaId?:"",
            title = it.title.toString(),
            subtitle = it.subtitle.toString(),
            songUrl = it.mediaUri.toString(),
            imageUrl = it.iconUri.toString()

        )
    }
}