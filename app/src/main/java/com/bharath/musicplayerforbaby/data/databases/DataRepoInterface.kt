package com.bharath.musicplayerforbaby.data.databases

import kotlinx.coroutines.flow.Flow

interface DataRepoInterface {

    suspend fun insert(songDb: SongDb)
    suspend fun getTopMediaId(): Flow<SongDb>

    suspend fun insertFav(favSongs: FavSongs)
     fun getFavsongs():Flow<List<FavSongs>>
    suspend fun deleteFav(favSongs: FavSongs)
}