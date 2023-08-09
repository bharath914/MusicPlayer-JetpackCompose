package com.bharath.musicplayer.exoplayer.callbacks

import android.widget.Toast
import com.bharath.musicplayer.exoplayer.MusicService
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player


/*
We use this class for updating the playerstate
If any error occurs while playing the song this class will help us to catch the error

 */
class MusicPlayerEventListener(
    private val musicService: MusicService,
) : Player.Listener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_READY && !playWhenReady) {
            musicService.stopForeground(false)
        }

    }


    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Unexpected Error", Toast.LENGTH_SHORT).show()
    }
}