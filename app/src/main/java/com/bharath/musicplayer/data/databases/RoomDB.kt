package com.bharath.musicplayer.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SongDb::class],
    version = 1
)
abstract class RoomDB : RoomDatabase() {
    abstract val dao: SongDao
}