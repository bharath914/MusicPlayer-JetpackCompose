package com.bharath.musicplayerforbaby.data

import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val  localAudio: LocalAudioInDetail
){


    suspend fun getAllSongDetails ():MutableList<DetailSong> = localAudio.getSongsWithMoreDetails()

}