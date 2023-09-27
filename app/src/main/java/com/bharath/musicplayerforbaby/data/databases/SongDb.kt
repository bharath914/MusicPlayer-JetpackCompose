package com.bharath.musicplayerforbaby.data.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.other.DataBaseConst as c

@Entity("SongsTable")
data class SongDb(
     @PrimaryKey(autoGenerate = true) val id: Int? = null,
     @ColumnInfo(name = "MediaId", defaultValue = c.DEFAULT_MEDIA_ID_VALUE) val mediaID: String,
     @ColumnInfo(name = "LastDuration") val lastDuration: Long = 0L,
)

@Entity("FavouriteSongs")
data class FavSongs(
   @PrimaryKey  val mediaId: String = "",
     val title: String = "",
     val albumName: String = "",
     val subtitle: String = "",
     val songUrl: String = "",
     val imageUrl: String = "",
     val duration: Long = 0L,
     val bitrate: String = "",
     val size: String = "",
     val mimeType: String = "",

     val albumartist: String = "",
     val dateAdded: String = "",
     val dateModified: String = "",
     val index:Int = 0
)