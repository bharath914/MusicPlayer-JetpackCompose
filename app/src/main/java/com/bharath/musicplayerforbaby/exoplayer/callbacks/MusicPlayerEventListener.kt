package com.bharath.musicplayerforbaby.exoplayer.callbacks

import android.widget.Toast
import com.bharath.musicplayerforbaby.exoplayer.MusicService
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class   MusicPlayerEventListener(
    private val musicService: MusicService
):Player.Listener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState ==Player.STATE_READY && !playWhenReady){
            musicService.stopForeground(false)
        }

    }
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Unexpected Error", Toast.LENGTH_SHORT).show()
    }
}