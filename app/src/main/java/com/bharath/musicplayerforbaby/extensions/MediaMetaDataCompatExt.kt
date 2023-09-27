package com.bharath.musicplayerforbaby.extensions

import android.support.v4.media.MediaMetadataCompat
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.data.databases.FavSongs

/*
This extension classes are used to reduce our work
for eg: MusicListScreen/Onclick -> we need to map our MediaMetadata to Normal Song Data Class instead
of doing it everytime we just call this function like : mediaItems.toSong()
 */
fun MediaMetadataCompat.toSong(): DetailSong {
    return description.let {
        DetailSong(
            mediaId = it.mediaId ?: "",
            title = it.title.toString(),
            artist = it.subtitle.toString(),
            songUrl = it.mediaUri.toString(),
            imageUrl = it.iconUri.toString()

        )
    }
}

fun DetailSong.toMetaData(): MediaMetadataCompat {

    return MediaMetadataCompat.Builder()

        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, songUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, imageUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, imageUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)

        .build()
}


fun FavSongs.toSong(): DetailSong {
    return DetailSong(
        mediaId,
        title,
        albumName,
        subtitle,
        songUrl,
        imageUrl,
        duration,
        bitrate,
        size,
        mimeType,
        albumartist,
        dateAdded
    )
}