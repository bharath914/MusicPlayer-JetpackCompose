package com.bharath.musicplayer.data.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songDb: SongDb)

    @Query("SELECT * FROM SongsTable Limit 1")
    fun getTopMediaId(): Flow<SongDb>

}