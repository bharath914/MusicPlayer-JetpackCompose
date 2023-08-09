package com.bharath.musicplayer.data.databases

import kotlinx.coroutines.flow.Flow

interface DataRepoInterface {

    suspend fun insert(songDb: SongDb)
    suspend fun getTopMediaId(): Flow<SongDb>
}