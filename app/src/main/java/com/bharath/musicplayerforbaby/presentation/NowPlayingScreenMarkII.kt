package com.bharath.musicplayerforbaby.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.exoplayer.isPlaying
import com.bharath.musicplayerforbaby.exoplayer.toSong
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun NowPlayingScreen2(
    musicListViewModel: MusicListViewModel = hiltViewModel(),
) {
    val playBackState = musicListViewModel.playbackstate.observeAsState()
    val curPlayingItem = musicListViewModel.curplayingSong.observeAsState()
    Surface(modifier = Modifier.fillMaxSize()) {


        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()

            )
            {
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(7f)
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                        .clip(RoundedCornerShape(5))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    curPlayingItem.value?.let {
                        AsyncImage(
                            model = it.description.iconUri,
                            contentDescription = "Current Playing Image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                        GlideImage(
                            model = it.description.iconUri,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        ){glide->
                            glide.load(it.description.iconUri)
                                .error(R.drawable.error_placeholder)
                                .sizeMultiplier(1.0f)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        }

                    }


                }
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    contentAlignment = Alignment.Center
                ) {

                    curPlayingItem.value?.let {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {


                            Text(
                                text = it.description.title.toString(),
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth(),

                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))


                            Text(
                                text = it.description.subtitle.toString(),
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth(),


                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Light,
                                fontSize = 15.sp

                            )
                            Spacer(modifier = Modifier.height(20.dp))

                        }
                    }

                }


            }
            Column(
                modifier = Modifier
                    .weight(2.2f)
                    .fillMaxWidth()

                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Part2NowPlaying()
            }


        }
    }
}

@Composable
fun Part2NowPlaying() {
    val vm: MusicListViewModel = hiltViewModel()
    val playBackState = vm.playbackstate.observeAsState()
    val curPlaySong = vm.curplayingSong.observeAsState()

    val iconState = remember {
        mutableStateOf(R.drawable.outline_play_circle_filled_24)
    }
    playBackState.value?.let {
        if (it.isPlaying) {
            iconState.value = R.drawable.outline_pause_circle_filled_24
        } else {
            iconState.value = R.drawable.outline_play_circle_filled_24
        }
    }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        curPlaySong.value?.let { media ->


            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(onClick = {
                    /// Add the repeat function here

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.repeat_icon),
                        contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    // Add the SkipToPrevious Function
                    vm.skipToPrevious()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filled_skip_previous),
                        contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    // Add the play and pause functions here

                    vm.playOrToggleTheSong(media.toSong(), true)
                }) {
                    Icon(
                        painter = painterResource(id = iconState.value),
                        contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    // Skip to the next song function
                    vm.skipToNext()

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filled_skip_next),
                        contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    // Shuffle Icon function

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.shuffle_icon),
                        contentDescription = ""
                    )
                }
            }
        }
    }

}