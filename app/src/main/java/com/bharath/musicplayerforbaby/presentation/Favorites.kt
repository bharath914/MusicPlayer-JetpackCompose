package com.bharath.musicplayerforbaby.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.bharath.musicplayerforbaby.data.databases.FavSongs
import com.bharath.musicplayerforbaby.extensions.toSong
import com.bharath.musicplayerforbaby.presentation.viewmodel.FavViewModel

@Immutable
class Favlist(val list: List<FavSongs>)
@Composable
fun Favorites(
    favViewModel: FavViewModel = hiltViewModel(),
) {
    Scaffold() {padding->
        val list = Favlist(favViewModel.favSongsList.collectAsState().value)


        LazyColumn(content = {
            items(list.list){
                MusicItem(song = it.toSong(), paddingValues =padding ) {

                }
            }
        })
    }
}