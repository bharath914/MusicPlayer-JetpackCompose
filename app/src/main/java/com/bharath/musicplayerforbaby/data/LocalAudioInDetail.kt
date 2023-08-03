package com.bharath.musicplayerforbaby.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext

class LocalAudioInDetail (@ApplicationContext val context: Context) {
    private val selection = MediaStore.Audio.Media.IS_MUSIC
    private val sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"
    private val mediaUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    fun getSongsWithMoreDetails(): MutableList<DetailSong> {
        val projectionDetail = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.SIZE,
            MediaStore.Audio.AudioColumns.MIME_TYPE,
            MediaStore.Audio.AudioColumns.BITRATE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
            MediaStore.Audio.AudioColumns.DATE_ADDED,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED



            )
        val detailsongs: MutableList<DetailSong> = mutableListOf()
        try {
            context.contentResolver?.query(mediaUri, projectionDetail, selection, null, sortOrder)
                .use { cursor ->

                    val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)


                    val albumIdColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val artistIDColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                    val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                    val bitrateC = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.BITRATE)

                    val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
                    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
                    val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(nameColumn)
                        val duration = cursor.getLong(durationColumn)
                        val mimeC = cursor.getString(mimeColumn)
                        val sizeC = cursor.getLong(sizeColumn)
                        val albumC = cursor.getString(albumColumn)
                        val bitrate = cursor.getLong(bitrateC)


                        val artist = cursor.getString(artistIDColumn)
                        val albumId = cursor.getLong(albumIdColumn)

                        val albumArtist = cursor.getString(albumArtistColumn)
                        val dateadded = cursor.getString(dateAddedColumn)
                        val dateModified = cursor.getString(dateModifiedColumn)


                        val uri =
                            ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        val albumarturi = ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        )
                        Log.d("SongsTag", "getAllSongsInDevice: $title")
                        val song = DetailSong(
                            mediaId = id.toString(),

                            title = title,

                            subtitle = artist,
                            imageUrl = albumarturi.toString(),

                            songUrl = uri.toString(),
                            duration = duration,
                            albumName = albumC,
//                            bitrate = bitrate.toString() ?: "",
//                            size = sizeC.toString()?:"",
//                            mimeType = mimeC ?:"",
//
//                            albumartist = albumArtist ?: "",
//                            dateAdded = dateadded ?:"",
//                            dateModified = dateModified ?:""
                        )

                        detailsongs.add(song)


                    }

                    cursor.close()


                }


        } catch (e: Exception) {
            e.printStackTrace()
        }




        return detailsongs
    }

}