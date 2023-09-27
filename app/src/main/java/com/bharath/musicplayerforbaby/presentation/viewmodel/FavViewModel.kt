package com.bharath.musicplayerforbaby.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.data.databases.DataRepoInterface
import com.bharath.musicplayerforbaby.data.databases.FavSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavViewModel @Inject constructor(
    private val repo: DataRepoInterface,
) : ViewModel() {

    val favSongsId
        get() = repo.getFavsongs()

    private val _favSongsList = MutableStateFlow(emptyList<FavSongs>())
    val favSongsList = _favSongsList.asStateFlow()


    init {


        viewModelScope.launch {

            repo.getFavsongs().collectIndexed { index, value ->
               _favSongsList.tryEmit(value)

            }

        }


    }
}