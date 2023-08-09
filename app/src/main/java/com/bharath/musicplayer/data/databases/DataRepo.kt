package com.bharath.musicplayer.data.databases

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


}