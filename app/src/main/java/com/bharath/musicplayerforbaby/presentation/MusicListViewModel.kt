package com.bharath.musicplayerforbaby.presentation


import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharath.musicplayerforbaby.data.Song
import com.bharath.musicplayerforbaby.exoplayer.MusicServiceConnection
import com.bharath.musicplayerforbaby.exoplayer.isPlayEnabled
import com.bharath.musicplayerforbaby.exoplayer.isPlaying
import com.bharath.musicplayerforbaby.exoplayer.isPrepared
import com.bharath.musicplayerforbaby.other.Const
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel
@Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,

    ) :
    ViewModel() {

    private val _mediaItems = MutableStateFlow<List<Song>>(listOf<Song>())
    val mediaItems: StateFlow<List<Song>> = _mediaItems

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()


    @OptIn(FlowPreview::class)
    val filterItems= searchText
        .onEach { _isSearching.update { true } }
        .debounce(250L)
        .combine(_mediaItems) { text, mediaitems->

        if (text.isBlank()){
            emptyList<Song>()
        }else{
            mediaitems.filter {
                it.deosMatchSearchQuery(text)
            }
        }

    }.onEach {
        _isSearching.update { false }
        }
        .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _mediaItems.value

    )


    val playbackstate = musicServiceConnection.playBackState
    val curplayingSong = musicServiceConnection.curPlayingSong

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    init {

        musicServiceConnection.subscribe(Const.MEDIA_ROOT_ID, object : SubscriptionCallback() {


            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>,
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(
                        mediaId = it.mediaId ?: "",
                        songUrl = it.description.mediaUri.toString() ?: "",
                        title = it.description.title.toString(),
                        subtitle = it.description.subtitle.toString(),
                        imageUrl = it.description.iconUri.toString() ?: "",
//                        duration = ,
//                        albumName = it.description.description.toString() ?: ""


                    )

                }
                _mediaItems.tryEmit(items)
            }
        })

    }

    fun playOrToggleTheSong(mediaItem: Song, toggle: Boolean) {
        val isPrepared = playbackstate.value?.isPrepared ?: false
        if (isPrepared &&
            mediaItem.mediaId == curplayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackstate.value?.let { playbackStateCompat ->
                when {
                    playbackStateCompat.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackStateCompat.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)

        }
    }

    fun skipToNext() {
        musicServiceConnection.transportControls.skipToNext()

    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(Const.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})

    }
}