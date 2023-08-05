package com.bharath.musicplayerforbaby.extensions

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING

/*
This variables will be inlined to the playback state
this states are used getting the exoplayer current state
this is very useful for updating the song duration and onclick in lazycolumn to play the song etc..
 */
inline val PlaybackStateCompat.isPrepared
    get() = state ==PlaybackStateCompat.STATE_BUFFERING ||
            state == PlaybackStateCompat.STATE_PLAYING ||
            state == PlaybackStateCompat.STATE_PAUSED
inline val PlaybackStateCompat.isPlaying
    get() = state ==PlaybackStateCompat.STATE_BUFFERING ||
            state == PlaybackStateCompat.STATE_PLAYING
inline val PlaybackStateCompat.isPlayEnabled
    get() = actions and PlaybackStateCompat.ACTION_PLAY != 0L ||
            (actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L
                    &&
                    state == PlaybackStateCompat.STATE_PAUSED
                    )
inline val PlaybackStateCompat.currentPlaybackPosition :Long
    get() = if (state == STATE_PLAYING){
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position+ (timeDelta*playbackSpeed)).toLong()
    }else{
        position
    }