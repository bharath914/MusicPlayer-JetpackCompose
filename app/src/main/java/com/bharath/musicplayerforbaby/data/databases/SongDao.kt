package com.bharath.musicplayerforbaby.data.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bharath.musicplayerforbaby.navigation.Screen
import kotlinx.coroutines.flow.Flow


@Dao
interface SongDao {
    @Insert(entity = SongDb::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songDb: SongDb)

    @Query("SELECT * FROM SongsTable Limit 1")
    fun getTopMediaId(): Flow<SongDb>

    // Favourites
    @Insert(entity = FavSongs::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavSongs(favSongs: FavSongs)

    @Delete(entity = FavSongs::class)
    suspend fun deleteFavSongs(favSongs: FavSongs)

    @Query("SELECT * FROM FavouriteSongs ORDER BY mediaId DESC")
     fun getFavorites():Flow<List<FavSongs>>


}