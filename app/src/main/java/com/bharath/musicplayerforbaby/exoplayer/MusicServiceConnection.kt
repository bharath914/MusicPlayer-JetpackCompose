package com.bharath.musicplayerforbaby.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bharath.musicplayerforbaby.other.Const.NETWORK_ERROR
import com.bharath.musicplayerforbaby.other.Event
import com.bharath.musicplayerforbaby.other.Resource


class MusicServiceConnection (
    context:Context
        ) {

    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected :LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError :LiveData<Event<Resource<Boolean>>> = _networkError


    private val _playBackState = MutableLiveData<PlaybackStateCompat ? >()
    val playBackState :LiveData<PlaybackStateCompat?> = _playBackState


    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat ? >()
    val curPlayingSong :LiveData<MediaMetadataCompat? > = _curPlayingSong

    lateinit var mediaController :MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    val transportControls: MediaControllerCompat.TransportControls
        get()= mediaController.transportControls


    fun subscribe(parentId :String, callback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentId,callback)
    }
    fun unsubscribe(parentId :String, callback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.unsubscribe(parentId,callback)
    }



    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ):MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
                mediaController = MediaControllerCompat(context,mediaBrowser.sessionToken).apply {
                    registerCallback(MediaControllerCallback())
                }
            _isConnected.postValue(Event(Resource.Success(true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.Error("The Connection was Suspended",false)))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(
                Event(Resource.Error(
                "Couldn't Connect To the Media Browser", false
            ))
            )
        }
    }
    private inner class MediaControllerCallback : MediaControllerCompat.Callback(){
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playBackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {

        _curPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event){
                NETWORK_ERROR -> {
                    _networkError.postValue(
                        Event(
                            Resource.Error("couldn't connect to the network .Please check your internet connection ", null)
                        )
                    )
                }
            }
        }

        override fun onSessionDestroyed() {
                mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }



}