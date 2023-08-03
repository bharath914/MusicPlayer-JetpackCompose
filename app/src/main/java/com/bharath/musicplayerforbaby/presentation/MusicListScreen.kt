package com.bharath.musicplayerforbaby.presentation

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.exoplayer.isPlaying
import com.bharath.musicplayerforbaby.exoplayer.toSong
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun MusicListScreen(
    musicListViewModel: MusicListViewModel = hiltViewModel(),
) {

    val songs = musicListViewModel.detailSongList.collectAsState()
    val settinglist = musicListViewModel.isSettingSortBy.collectAsState()
    val nsongs = songs.value

    val curPlayingSong = musicListViewModel.curplayingSong.collectAsState().value

    val sheetstate = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()
    Scaffold() {


        ModalBottomSheetLayout(
            sheetElevation = 50.dp,
            sheetState = sheetstate,
            sheetContent = {
                NowPlayingScreen2()
            },
            sheetShape = MaterialTheme.shapes.large,
            modifier = Modifier.padding(it)

        ) {


            androidx.compose.material.Scaffold(
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.background,
                    ) {
                        curPlayingSong?.let { mediaMetaDat ->

                            AnimatedContent(targetState = mediaMetaDat) { media ->
                                BottomMusicIndicator(media = media, openDrawer = {
                                    scope.launch {

                                        sheetstate.show()

                                    }
                                }) { clicked ->
                                    musicListViewModel.playOrToggleTheSong(clicked.toSong(), true)
                                }

                            }

                        }
                    }

                },
                scaffoldState = scaffoldState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .statusBarsPadding(), drawerContent = {

                    DrawerContent() {
                        scope.launch {

                            scaffoldState.drawerState.close()
                        }
                    }
                },
                topBar = {
                    SearchFun() {
                        scope.launch {

                            scaffoldState.drawerState.open()
                        }
                    }
                },
                content = { padding ->
                    val scaffoldstateSort = rememberBottomSheetScaffoldState()



                    Column(Modifier.padding(paddingValues = padding)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .padding(start = 15.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = CenterVertically
                        ) {

                            Text(
                                text = "Date Modified",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alpha(0.7f).clickable {
                                                                          scope.launch {
                                                                              scaffoldstateSort.bottomSheetState.show()
                                                                          }
                                },
                                fontSize = 14.sp
                            )

                            Box() {


                                Row(verticalAlignment = CenterVertically) {


                                    IconButton(onClick = {

                                        val start = 0
                                        val end = songs.value.size ?: 0
                                        val randm = (start..end).random()
                                        musicListViewModel.playOrToggleTheSong(
                                            songs.value[randm],
                                            true
                                        )


                                    }, modifier = Modifier.size(24.dp)) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.shuffle_icon),
                                            contentDescription = ""
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${songs.value.size} Songs",

                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.alpha(0.8f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (settinglist.value) {

                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {

                                LinearProgressIndicator()
                            }

                        }



                            LazyColumn {

                                items(nsongs) {

                                    MusicItem(song = it, padding) {
                                        musicListViewModel.playOrToggleTheSong(it, false)
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }

                    }
                }, backgroundColor = MaterialTheme.colorScheme.background,
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerScrimColor = MaterialTheme.colorScheme.primary,
                drawerGesturesEnabled = false

            )
        }

    }
}

@Composable
fun SortOptions() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "DateModified")
    }
}

@Composable
fun DrawerContent(onclickDrawerCLose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {

        IconButton(onClick = { onclickDrawerCLose() }) {
            Icon(Icons.Filled.Close, contentDescription = "")
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BottomMusicIndicator(
    media: MediaMetadataCompat,
    openDrawer: () -> Unit,
    onclickIcon: (MediaMetadataCompat) -> Unit,
) {
    val musicListViewModel: MusicListViewModel = hiltViewModel()
    val playbackstate = musicListViewModel.playbackstate.collectAsState()
    var icon = remember {
        mutableStateOf(R.drawable.play_arrow)
    }
    playbackstate.value?.let {
        if (it.isPlaying) {
            icon.value = R.drawable.pause_circle
        } else {
            icon.value = R.drawable.play_arrow
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color.Black,
                    shape = RoundedCornerShape(15)
                )
                .size(64.dp)
        ) {

            GlideImage(
                model = media.description.iconUri,
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(15))
                    .clickable { openDrawer() }
            ) {
                it.load(media.description.iconUri)
                    .error(R.drawable.error_placeholder)
                    .override(512)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)


            }
        }
        Spacer(modifier = Modifier
            .width(20.dp)
            .clickable { openDrawer() })
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = media.description.title.toString(), maxLines = 1,
                overflow = TextOverflow.Ellipsis, modifier = Modifier
                    .weight(0.8f)
                    .clickable { openDrawer() }
            )
            IconButton(onClick = {
                onclickIcon(media)
            }) {
                Icon(painter = painterResource(id = icon.value), contentDescription = "")
            }
        }
    }

}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MusicItem(
    song: DetailSong,
    paddingValues: PaddingValues,
    onclick: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
            .height(48.dp)
            .clickable {
                onclick()
            }
    ) {
        val imageurl = song.imageUrl
        val colorBl = Color(0xED1B1A1A)

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(colorBl, shape = RoundedCornerShape(15))
        ) {

            GlideImage(
                model = imageurl,
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15)),
                alignment = Center,
                alpha = 0.7f
            ) {
                it.load(imageurl)
                    .override(128)
                    .placeholder(R.drawable.error_placeholder)
                    .timeout(2000)
                    .error(R.drawable.error_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Text(
                text = song.title,
                maxLines = 1,

                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.alpha(0.8f),
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,

                )
            Text(
                text = song.subtitle,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(0.7f),
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,

                )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFun(onclickForDrawer: () -> Unit) {
    val musicListViewModel: MusicListViewModel = hiltViewModel()

    val searchText by musicListViewModel.searchText.collectAsState()
    val isSearching by musicListViewModel.isSearching.collectAsState()
    val filterSongs = musicListViewModel.filterItems.collectAsState()
    val text = remember {
        mutableStateOf("")
    }
    var active: Boolean by remember { mutableStateOf(false) }
    Spacer(modifier = Modifier.height(8.dp))
    SearchBar(
        query = text.value,
        onQueryChange = {
            musicListViewModel.onSearchTextChange(it)
            text.value = it
        },
        onSearch = {

        },
        active = active,
        onActiveChange = {
            active = it
        },
        leadingIcon = {
            if (!active) {
                IconButton(onClick = {
                    onclickForDrawer()
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = "")
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                if (active) {
                    if (text.value.isNotEmpty()) {
                        text.value = ""
                    }
                    active = !active
                }
            }) {

                Icon(
                    if (active) Icons.Filled.Close else Icons.Filled.Search, contentDescription = ""
                )
            }
        },
        placeholder = {
            Text(text = "Search For Music", fontWeight = FontWeight.Light, fontSize = 18.sp)
        },
        modifier = Modifier

            .fillMaxWidth(),


        ) {
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                CircularProgressIndicator()

            }
        } else {

            LazyColumn {
                items(filterSongs.value) { song ->

                    MusicItem(song = song, paddingValues = PaddingValues(4.dp)) {
                        musicListViewModel.playOrToggleTheSong(song, toggle = true)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}