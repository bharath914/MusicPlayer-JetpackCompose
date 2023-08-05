package com.bharath.musicplayerforbaby.presentation


import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.data.DurationAndOther
import com.bharath.musicplayerforbaby.datastore.DataStorePrefRepo
import com.bharath.musicplayerforbaby.exoplayer.LocalMusicSource
import com.bharath.musicplayerforbaby.exoplayer.MusicServiceConnection
import com.bharath.musicplayerforbaby.extensions.currentPlaybackPosition
import com.bharath.musicplayerforbaby.extensions.isPlayEnabled
import com.bharath.musicplayerforbaby.extensions.isPlaying
import com.bharath.musicplayerforbaby.extensions.isPrepared
import com.bharath.musicplayerforbaby.other.Const
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val datastore :DataStorePrefRepo


    ) :
    ViewModel() {

    /*
    list of all the songs in the device
     */
    private val _detailSong = MutableStateFlow(emptyList<DetailSong>())
    val detailSongList: StateFlow<List<DetailSong>> = _detailSong


    /*
    Fields required for the UI
     */
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private val _isShuffleMode = MutableStateFlow(false)
    val isShuffleMode: StateFlow<Boolean> = _isShuffleMode


    /*
    Below fields are useful for sorting the list
    according to the user such as name, datemodified , year etc.
     */

    private var sortNumber = MutableStateFlow(0)

    private val _isSettingSortBy = MutableStateFlow(false)
    val isSettingSortBy = _isSettingSortBy.asStateFlow()

    private val _isSettingSortByValue = MutableStateFlow(0)
    val isSettingSortByValue = _isSettingSortByValue.asStateFlow()

    /*
    Don't worry by looking the below code
    it is advanced but try to understand it clearly
    We are combining the flow of searchtext which the text we enter in SearchBar
    and if the searchtext changes then only we perform this search operation
    debounce is something similar to delay but it will emit the required value after given time ( 1000millis)
    now we combining the searchtext and songdetail list
     */
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
        .combine(_detailSong) { cons, song ->
            when (cons) {
                0 -> {
                    _detailSong.value.sortedBy {
                        it.dateModified
                    }
                }

                1 -> {
                    _detailSong.value.sortedBy {
                        it.dateModified
                    }.reversed()
                }

                2 -> {
                    _detailSong.value.sortedBy {
                        it.title

                    }
                }

                else -> {
                    _detailSong.value
                }
            }

        }.onEach {
            _isSettingSortBy.update { false }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(2000), _detailSong.value)

    /*
    Duration of the Current Song
     */


    private val _currDurationOfTheSong = MutableStateFlow(0L)
    val currDurationOfTheSong = _currDurationOfTheSong.asStateFlow()
    private var shouldUpdate = true

    /*
    Helps in finding the duration and album artist and  size and mimetype and bitrate(android 11 and above)

     */
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
    val detailsOfTheSong = _detailsofthesong.asStateFlow()

    /*
    These two are very important in updating everything the player ui
    these are must and they are connected to the music service connection's values
     */
    val playbackstate = musicServiceConnection.playBackState
    val curplayingSong = musicServiceConnection.curPlayingSong


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    init {
        /*
        We are connecting / subscribing to the music service here only after subscribing
        we will get the list of the songs this will help in improving user experience
         */

        musicServiceConnection.subscribe(Const.MEDIA_ROOT_ID, object : SubscriptionCallback() {


            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>,
            ) {
                super.onChildrenLoaded(parentId, children)

                viewModelScope.launch {

                    _detailSong.tryEmit(localMusicSource.allSongs)
                    datastore.readSortKey().collectLatest {
                        sortNumber.tryEmit(it)
                    }
                }



            }
        })


        setExtraDetailsOfTheSong()
        setCurrentDuration()
    }

    /*

     */
     fun setSortKey(){
        _isSettingSortBy.tryEmit(true)
        viewModelScope.launch(Dispatchers.IO) {

        datastore.saveSortKey(0)
        }
        _isSettingSortBy.tryEmit(false)

    }
    private fun setExtraDetailsOfTheSong() {


        /*
        This function will provide us the required
        extra values such as duration , bitrate , size ,
        mime type to implement them in our UI
         */
        viewModelScope.launch {
            curplayingSong.collectLatest { mediacompat ->
                _detailSong.value.find { detail ->
                    detail.title == mediacompat?.description?.title

                }?.let {




                    _detailsofthesong.tryEmit(
                        DurationAndOther(
                            duration = it.duration,
                            bitrate = it.bitrate,
                            mimeType = it.mimeType,
                            size = it.size,
                            samplingRate = "",
                            albumName = it.albumName,
                            albumArtist = it.albumartist
                        )

                    )

                }
            }
        }

    }


    private fun setCurrentDuration() {

        /*
        this is used to update the player position of the current song
        me using shouldUpdate variable here but is not mandatory because we are launching it in the viewmodelscope
         */
        viewModelScope.launch {
            if (playbackstate.value?.isPlaying == true){
                _currDurationOfTheSong.tryEmit(playbackstate.value!!.currentPlaybackPosition)
            }
            delay(500L)
            if (shouldUpdate){
                setCurrentDuration()
            }

        }


    }

    fun playOrToggleTheSong(mediaItem: DetailSong, toggle: Boolean) {
        /*
        This function will be used for toggling the song
         */
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
        curplayingSong.value?.let {
            Log.d("MEdiaID", ":${it.description.mediaId} ")
        }
    }
    /*
    Basic functions for the exoplayer
     */

    fun skipToNext() {
        musicServiceConnection.transportControls.skipToNext()


    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(l:Long){
        musicServiceConnection.transportControls.seekTo(l)
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
        shouldUpdate = false
        curplayingSong.value?.let {
        viewModelScope.launch {
            datastore.saveMediaId(it.description.mediaId!!)

        }
        }
        musicServiceConnection.unsubscribe(Const.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})

    }
}