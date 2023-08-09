package com.bharath.musicplayer.data.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bharath.musicplayer.other.DataBaseConst as c

@Entity("SongsTable")
data class SongDb(
     @PrimaryKey(autoGenerate = true) val id: Int? = null,
     @ColumnInfo(name = "MediaId", defaultValue = c.DEFAULT_MEDIA_ID_VALUE) val mediaID: String,
     @ColumnInfo(name = "LastDuration") val lastDuration: Long = 0L,
)