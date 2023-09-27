package com.bharath.musicplayerforbaby.data.databases

import kotlinx.coroutines.flow.Flow

class DataRepo(
    private val dao: SongDao,
) : DataRepoInterface {
    override suspend fun insert(songDb: SongDb) {
        dao.insert(songDb)
    }

    override suspend fun getTopMediaId(): Flow<SongDb> {
        return dao.getTopMediaId()
    }

    override suspend fun insertFav(favSongs: FavSongs) {
        dao.insertFavSongs(favSongs)
    }

    override  fun getFavsongs(): Flow<List<FavSongs>> {
        return dao.getFavorites()
    }

    override suspend fun deleteFav(favSongs: FavSongs) {
        dao.deleteFavSongs(favSongs)
    }


}