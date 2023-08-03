package com.bharath.musicplayerforbaby.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.data.Song

fun    MediaMetadataCompat.toSong(): DetailSong {
    return description.let {
        DetailSong(
            mediaId = it.mediaId?:"",
            title = it.title.toString(),
            subtitle = it.subtitle.toString(),
            songUrl = it.mediaUri.toString(),
            imageUrl = it.iconUri.toString()

        )
    }
}
fun DetailSong.toMetaData(): MediaMetadataCompat {

    return MediaMetadataCompat.Builder()

        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, songUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, imageUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, imageUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)

        .build()
}