package com.bharath.musicplayerforbaby.presentation

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.data.databases.FavSongs
import com.bharath.musicplayerforbaby.extensions.convertMillisecondsToMinutesAndSeconds
import com.bharath.musicplayerforbaby.extensions.formatMimeType
import com.bharath.musicplayerforbaby.extensions.isPlaying
import com.bharath.musicplayerforbaby.extensions.toSong
import com.bharath.musicplayerforbaby.presentation.viewmodel.MusicListViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun NowPlayingScreen2(
    musicListViewModel: MusicListViewModel = hiltViewModel(),
) {
    val playBackState = musicListViewModel.playbackstate.collectAsState()
    val curPlayingItem = musicListViewModel.curplayingSong.collectAsState()




    Surface(
        modifier = Modifier
            .fillMaxSize()

    ) {


        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .fillMaxSize().background(MaterialTheme.colorScheme.background)
                    ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){

                    curPlayingItem.value?.let { media ->
                        GlideImage(
                            model = media.description.iconUri,
                            contentDescription = "",
                            modifier = Modifier.blur(60.dp).fillMaxSize().alpha(0.6f)
                        ) {
                            it.load(media.description.iconUri)
                                .centerCrop()
                                .error(R.drawable.error_placeholder)
                                .sizeMultiplier(1.0f)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        }
                    }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(Modifier.fillMaxSize()) {


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
                                .background(MaterialTheme.colorScheme.background)

                        ) {
                            curPlayingItem.value?.let {

                                GlideImage(
                                    model = it.description.iconUri,
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds
                                ) { glide ->
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),

                                        textAlign = TextAlign.Center,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))


                                    Text(
                                        text = it.description.subtitle.toString(),
                                        maxLines = 1,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(),


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
    }

}

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun Part2NowPlaying() {
    val vm: MusicListViewModel = hiltViewModel()
    val playBackState = vm.playbackstate.collectAsState()
    val curPlaySong = vm.curplayingSong.collectAsState()
    val shuffleEnabled = vm.isShuffleMode.collectAsState()
    val repeatEnabled = vm.isRepeatMode.collectAsState()

    val timeline = vm.currDurationOfTheSong.collectAsState(initial = 0L)
    val iconState = remember {
        mutableStateOf(R.drawable.outline_play_circle_filled_24)
    }
    val extraDetails = vm.detailsOfTheSong.collectAsState()
    playBackState.value?.let {
        if (it.isPlaying) {
            iconState.value = R.drawable.outline_pause_circle_filled_24
        } else {
            iconState.value = R.drawable.outline_play_circle_filled_24
        }
    }
    val repeaticon = remember {
        mutableStateOf(R.drawable.repeat_icon)
    }
    repeatEnabled.value.let {
        if (it) {
            repeaticon.value = R.drawable.repeat_one
        } else {
            repeaticon.value = R.drawable.repeat_icon
        }
    }

    val shuffleAlpha = remember {
        mutableStateOf(1f)
    }
    shuffleEnabled.value.let {
        if (it) {

            shuffleAlpha.value = 1f
        } else {

            shuffleAlpha.value = 0.2f
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
                    vm.repeatSongs()

                }) {
                    Icon(
                        painter = painterResource(repeaticon.value),
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
                    AnimatedContent(targetState = iconState.value) {

                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = ""
                        )
                    }
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
                    vm.shuffleSongs()

                }) {
                    AnimatedContent(targetState = shuffleAlpha.value) {


                        Icon(
                            painter = painterResource(id = R.drawable.shuffle_icon),
                            contentDescription = "",
                            modifier = Modifier.alpha(it)

                        )
                    }
                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {


        Column(verticalArrangement = Arrangement.Top) {


            Slider(
                value = timeline.value.toFloat(),
                onValueChange = {
                    vm.seekTo(it.toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onSurface,

                    activeTrackColor = MaterialTheme.colorScheme.onSurface
                ),


                valueRange = 0f..extraDetails.value.duration.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)


            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = convertMillisecondsToMinutesAndSeconds(timeline.value),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = convertMillisecondsToMinutesAndSeconds(extraDetails.value.duration),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Text(
                    text = extraDetails.value.albumName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.basicMarquee(delayMillis = 500)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = buildAnnotatedString {
                        append(formatMimeType(extraDetails.value.mimeType))


                    },
                    fontWeight = FontWeight.Thin,
                    fontSize = 14.sp,
                    modifier = Modifier.alpha(0.7f)

                )
            }


        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = {
            // Favourite Functions
                    curPlaySong.value?.let {
                        val cursong=it.toSong()
                        vm.saveToFav(FavSongs(
                            mediaId = cursong.mediaId,
                            title = it.description.title.toString(),
                            albumName = cursong.albumName,
                            subtitle = cursong.artist,
                            songUrl = cursong.songUrl,
                            imageUrl = cursong.imageUrl,
                            duration = cursong.duration,
                            bitrate = cursong.bitrate,
                            size = cursong.size,
                            mimeType = cursong.mimeType,
                            albumartist = cursong.albumartist,
                            dateAdded = cursong.dateAdded,
                            dateModified = cursong.dateModified,
//                            index = cursong.index
                        ))
                    }
            }) {
                Icon(painterResource(id = R.drawable.favorite), contentDescription = "")
            }
            IconButton(onClick = {

                // Info Functions
            }) {
                Icon(Icons.Filled.Info, contentDescription = "")
            }
            IconButton(onClick = {
                        // Time Functions
            }) {
                Icon(Icons.Filled.Timelapse, contentDescription = "")
            }
        }
    }
}
