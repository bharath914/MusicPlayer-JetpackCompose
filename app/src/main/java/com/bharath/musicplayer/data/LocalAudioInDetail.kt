package com.bharath.musicplayer.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext

/*
This class will use content resolver which is android's tool to get the required content from the
device
 */
class LocalAudioInDetail(
    @ApplicationContext val context: Context,

    ) {
    // selection is nothing but instead of voice recording and ringtones we only select the music files
    private val selection = MediaStore.Audio.Media.IS_MUSIC

    // sort order for our songs
    private val sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

    // media uri just the path to where the cursor needs to search for the songs
    private val mediaUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }


    //function to search songs in the device
    fun getSongsWithMoreDetails(): MutableList<DetailSong> {
        val projectionDetail = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.SIZE,
            MediaStore.Audio.AudioColumns.MIME_TYPE,

            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
            MediaStore.Audio.AudioColumns.DATE_ADDED,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,
//            MediaStore.Audio.AudioColumns.BITRATE


        )
        val detailsongs: MutableList<DetailSong> = mutableListOf()
        // now study this class carefully
        try {
            context.contentResolver?.query(mediaUri, projectionDetail, selection, null, sortOrder)
                .use { cursor ->
                    /*
                    In Your device If you download any song the song metadata will be stored in different
                    columns just like the normal database
                    so we need a tool called cursor for searching these data base queries
                    first we provide each column to the cursor and then we add the details in that particular
                    column
                     */

                    val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)


                    val albumIdColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val artistIDColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                    val mimeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE)
                    val sizeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
                    val albumColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
//                    val bitrateC = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.BITRATE)

                    val albumArtistColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
                    val dateAddedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)

                    // if the cursor have the next row to search
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(nameColumn)
                        val duration = cursor.getLong(durationColumn)
                        val mimeC = cursor.getString(mimeColumn)
                        val sizeC = cursor.getLong(sizeColumn)
                        val albumC = cursor.getString(albumColumn)
//                        val bitrate = cursor.getLong(bitrateC)


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

                        val song = DetailSong(
                            mediaId = id.toString(),

                            title = title,

                            subtitle = artist,
                            imageUrl = albumarturi.toString(),

                            songUrl = uri.toString(),
                            duration = duration,
                            albumName = albumC,
                            bitrate = "",
                            size = sizeC.toString(),
                            mimeType = mimeC ?: "",

                            albumartist = albumArtist ?: "",
                            dateAdded = dateadded ?: "",
                            dateModified = dateModified ?: ""
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

    private fun calculteBitr(duration: Long, sizeC: Long): String {

        val bit = sizeC / duration
        Log.d("Bitrate", bit.toString())
        return "$bit"
    }

}