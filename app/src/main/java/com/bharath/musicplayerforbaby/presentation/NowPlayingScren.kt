//package com.bharath.musicplayerforbaby.presentation
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.IntrinsicSize
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Slider
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.AsyncImage
//import com.bharath.musicplayerforbaby.R
//import com.bharath.musicplayerforbaby.exoplayer.isPlaying
import com.bharath.musicplayerforbaby.exoplayer.toSong
//
//@Composable
//fun NowPlayingScreen(
//    musicListViewModel: MusicListViewModel = hiltViewModel(),
//) {
//    val playbackstate = musicListViewModel.playbackstate.observeAsState()
//    val curPlayingItem = musicListViewModel.curplayingSong.observeAsState()
//    Surface() {
//        Column(modifier = Modifier.fillMaxSize()) {
//
//            curPlayingItem.value?.let { media ->
//
//                Spacer(modifier = Modifier.height(40.dp))
//
//                Box(
//                    modifier = Modifier
//
//                        .fillMaxWidth()
//                        .padding(start = 30.dp, end = 30.dp)
//
//                ) {
//
//
//                    AsyncImage(
//                        model = media.description.iconUri,
//                        contentDescription = "",
//                        contentScale = ContentScale.FillBounds,
//                        modifier = Modifier
//                            .size(300.dp)
//                            .clip(RoundedCornerShape(15.dp)),
//
//
//                        )
//
//                }
//                Spacer(modifier = Modifier.height(25.dp))
//                Text(
//                    text = media.description.title.toString(),
//                    style = MaterialTheme.typography.headlineMedium,
//                    maxLines = 1,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 30.dp, end = 30.dp),
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(15.dp))
//                Text(
//                    text = media.description.subtitle.toString(),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 35.dp, end = 35.dp),
//                    textAlign = TextAlign.Center,
//                    maxLines = 1,
//                    style = MaterialTheme.typography.bodyLarge
//                )
//
//
//                Slider(
//                    value = 0f, onValueChange = {
//
//                    }, modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(40.dp)
//                )
//
//                val icon = remember {
//                    mutableStateOf(R.drawable.large_play)
//                }
//                playbackstate.value?.let {
//                    if (it.isPlaying) {
//                        icon.value = R.drawable.large_pause_circle
//                    } else {
//                        icon.value = R.drawable.large_play
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(30.dp))
//                Row(modifier = Modifier.fillMaxWidth().height(intrinsicSize = IntrinsicSize.Max).padding(start = 30.dp, end = 30.dp)) {
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .fillMaxHeight()
//                            .background(MaterialTheme.colorScheme.primary)
//                    ) {
//                        IconButton(
//                            onClick = {
//                                musicListViewModel.skipToPrevious()
//                            },
//                            modifier = Modifier.size(84.dp)
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.large_skip_previous),
//                                contentDescription = "",
//
//                                )
//
//                        }
//                    }
//                    Box(
//                        modifier = Modifier
//                            .weight(2f)
//                            .background(MaterialTheme.colorScheme.secondary),
//                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        IconButton(onClick = {
//                            musicListViewModel.playOrToggleTheSong(media.toSong(), true)
//                        }, modifier = Modifier.size(100.dp)) {
//                            Icon(
//                                painter = painterResource(id = icon.value),
//                                contentDescription = "",
//
//
//                                )
//
//                        }
//                    }
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .background(MaterialTheme.colorScheme.tertiary)
//                    ) {
//                        IconButton(onClick = {
//                            musicListViewModel.skipToNext()
//                        }, modifier = Modifier.size(84.dp)) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.large_skip_next),
//                                contentDescription = "",
//
//                                )
//                        }
//
//                    }
//
//
//                }
//
//
//            }
//        }
//
//    }
//
//}