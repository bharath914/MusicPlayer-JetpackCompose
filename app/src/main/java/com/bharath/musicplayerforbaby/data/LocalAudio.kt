package com.bharath.musicplayerforbaby.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext

class LocalAudio(@ApplicationContext val context: Context) {


       private val songs: MutableList<Song> = mutableListOf()

       private val mediaUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

       private val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.DURATION
        )
       private val selection = MediaStore.Audio.Media.IS_MUSIC
       private val sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

   fun getAllSongsInDevice(): MutableList<Song> {
        try {
            context.contentResolver?.query(mediaUri, projection, selection, null, sortOrder)
                .use { cursor ->

                    val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)


                    val albumNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                    val albumIdColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val artistIDColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    while (cursor.moveToNext()){
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(nameColumn)
                        val albumName  = cursor.getString(albumNameColumn)
                        val duration = cursor.getLong(durationColumn)
                        val artist = cursor.getString(artistIDColumn)
                        val albumId = cursor.getLong(albumIdColumn)
                        val uri =
                            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                        val albumarturi = ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        )
                        Log.d("SongsTag", "getAllSongsInDevice: $title")
                        val song = Song(
                            mediaId = id.toString(),

                            title = title,
//                            albumName = albumName,
                            subtitle = artist,
                            imageUrl = albumarturi.toString(),
                            duration = duration,
                            songUrl = uri.toString()
                        )
                        songs.add(song)




                    }
                    if (songs.isEmpty()) {
                        Toast.makeText(context, "No Songs In the Device", Toast.LENGTH_SHORT).show()
                    }
                        cursor.close()


                }


        } catch (e: Exception) {
            e.printStackTrace()
        }




        return songs

    }

}