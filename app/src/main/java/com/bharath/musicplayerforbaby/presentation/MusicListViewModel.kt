package com.bharath.musicplayerforbaby.presentation


import android.media.MediaExtractor
import android.media.MediaFormat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.data.DurationAndOther
import com.bharath.musicplayerforbaby.exoplayer.LocalMusicSource
import com.bharath.musicplayerforbaby.exoplayer.MusicServiceConnection
import com.bharath.musicplayerforbaby.exoplayer.currentPlaybackPosition
import com.bharath.musicplayerforbaby.exoplayer.isPlayEnabled
import com.bharath.musicplayerforbaby.exoplayer.isPlaying
import com.bharath.musicplayerforbaby.exoplayer.isPrepared
import com.bharath.musicplayerforbaby.other.Const
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicListViewModel
@Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val localMusicSource: LocalMusicSource,

    ) :
    ViewModel() {
    private val _detailSong = MutableStateFlow(emptyList<DetailSong>())
    val detailSongList: StateFlow<List<DetailSong>> = _detailSong


    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private val _isShuffleMode = MutableStateFlow(false)
    val isShuffleMode: StateFlow<Boolean> = _isShuffleMode


    private val _isSettingSortBy = MutableStateFlow(false)
    val isSettingSortBy = _isSettingSortBy.asStateFlow()
    private val _isSettingSortByValue = MutableStateFlow(0)
    val isSettingSortByValue = _isSettingSortByValue.asStateFlow()

    @OptIn(FlowPreview::class)
    val filterItems = searchText
        .onEach { _isSearching.update { true } }
        .debounce(1000)
        .combine(_detailSong) { text, mediaitems ->

            if (text.isBlank()) {
                emptyList<DetailSong>()
            } else {
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
            _detailSong.value

        )

    @OptIn(FlowPreview::class)
    val dynamicSortedList = isSettingSortByValue.onEach { _isSettingSortBy.update { true } }
        .debounce(250L)
        .combine(_detailSong){cons,song->
            when(cons){
                0 ->{
                    _detailSong.value.sortedBy {
it.dateModified
                    }
                }
                1 ->{
                    _detailSong.value.sortedBy {
                        it.dateModified
                    }.reversed()
                }
                2 ->{
                    _detailSong.value.sortedBy {
                        it.title

                    }
                }
                else->{
                    _detailSong.value
                }
            }

        }.onEach {
            _isSettingSortBy.update { false }
        }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(2000),_detailSong.value)























    private val _durationOfTheSong = MutableStateFlow(0L)
    val durationOfTheSong = _durationOfTheSong.asStateFlow()
    private val _currDurationOfTheSong = MutableStateFlow(0L)
    val currDurationOfTheSong = _currDurationOfTheSong.asStateFlow()
    private val _detailsofthesong = MutableStateFlow(
        DurationAndOther(
            duration = 0L,
            bitrate = "",
            mimeType = "",
            size = "",
            samplingRate = "",
            albumArtist = "",
            albumName = ""
        )
    )
    val detailsofthesong = _detailsofthesong.asStateFlow()
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

                _detailSong.tryEmit(localMusicSource.allSongs)

            }
        })

        setDurationOfTheItem()
    }

    private fun setDurationOfTheItem() {


        viewModelScope.launch {
            curplayingSong.collectLatest { mediacompat ->
                _detailSong.value.find { detail ->
                    detail.title == mediacompat?.description?.title

                }?.let {
                    _durationOfTheSong.tryEmit(
                        it.duration
                    )

                    val mex = MediaExtractor()
                    var sample = 44
                    try {
                        mex.setDataSource(it.songUrl)
                        val mf = mex.getTrackFormat(0)
                        sample = mf.getInteger(MediaFormat.KEY_BIT_RATE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    _detailsofthesong.tryEmit(
                        DurationAndOther(
                            duration = it.duration,
                            bitrate = it.bitrate,
                            mimeType = it.mimeType,
                            size = it.size,
                            samplingRate = sample.toString(),
                            albumName = it.albumName,
                            albumArtist = it.albumartist
                        )

                    )

                }
            }
        }

    }


    private fun setCurrentDuration() {
        viewModelScope.launch {

            playbackstate.collectLatest {
                it?.let {
                    if (it.isPlaying || it.isPrepared || it.isPlayEnabled) {
                        viewModelScope.launch {
                            var condition = true
                            while (condition) {
                                if (it.position > 0) {
                                    _currDurationOfTheSong.tryEmit(it.currentPlaybackPosition)
                                    if (!it.isPlaying) {
                                        condition = false
                                    }
                                }
                                delay(1000)
                            }

                        }
                    }
                }
            }
        }
    }

    fun playOrToggleTheSong(mediaItem: DetailSong, toggle: Boolean) {
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

    fun shuffleSongs() {
        if (_isShuffleMode.value) {
            musicServiceConnection.transportControls.setShuffleMode(SHUFFLE_MODE_NONE)
            _isShuffleMode.tryEmit(false)
        } else {

            musicServiceConnection.transportControls.setShuffleMode(SHUFFLE_MODE_ALL)
            _isShuffleMode.tryEmit(true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(Const.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})

    }
}