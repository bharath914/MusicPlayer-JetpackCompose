package com.bharath.musicplayerforbaby.data

import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val  localAudio: LocalAudio
){


    suspend fun getAllSongs():List<Song> = localAudio.getAllSongsInDevice()
    suspend fun getAllSongDetails ():MutableList<DetailSong> = localAudio.getSongsWithMoreDetails()

}