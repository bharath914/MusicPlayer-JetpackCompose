package com.bharath.musicplayerforbaby.presentation

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.data.DetailSong
import com.bharath.musicplayerforbaby.extensions.isPlaying
import com.bharath.musicplayerforbaby.extensions.toSong
import com.bharath.musicplayerforbaby.navigation.NavConst
import com.bharath.musicplayerforbaby.presentation.signIn.GoogleAuthClient
import com.bharath.musicplayerforbaby.presentation.viewmodel.MusicListViewModel
import com.bharath.musicplayerforbaby.presentation.viewmodel.SignInViewModel
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Immutable
class ImmutableList(val list: List<DetailSong>)

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalPagerApi::class
)
@Composable
fun MusicListScreen(
    musicListViewModel: MusicListViewModel = hiltViewModel(),
    navcontroller: NavHostController,
) {

    val songs = musicListViewModel.dynamicSortedList.collectAsState()
    val il = ImmutableList(songs.value)
    val settinglist = musicListViewModel.isSettingSortBy.collectAsState()


    val curPlayingSong = musicListViewModel.curplayingSong.collectAsState().value

    val sheetstate = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()
    Scaffold { it ->


        ModalBottomSheetLayout(
            sheetElevation = 50.dp,
            sheetState = sheetstate,
            sheetContent = {
                NowPlayingScreen2()
            },
            sheetShape = MaterialTheme.shapes.large,
            modifier = Modifier.padding(it)

        ) {


            val pagerstate = rememberPagerState()
            val inter = pagerstate.interactionSource.interactions

            var bgclr = Color(0xFFFCFCFC)
            if (isSystemInDarkTheme()) {
                bgclr = Color(0xFF131212)
            }
            val mbg = MaterialTheme.colorScheme.surface
            androidx.compose.material.Scaffold(
                bottomBar = {
                    BottomAppBar(
                        containerColor = bgclr,
                        tonalElevation = 25.dp,
                        modifier = Modifier.clip(RoundedCornerShape(15)),

                        ) {


                        curPlayingSong?.let { mediaMetaDat ->

                            AnimatedContent(
                                targetState = mediaMetaDat,
                                label = "bottomBar"
                            ) { media ->
                                BottomMusicIndicator(media = media, openDrawer = {
                                    scope.launch {

                                        sheetstate.show()

                                    }
                                }) { clicked ->
                                    musicListViewModel.playOrToggleTheSong(
                                        clicked.toSong(),
                                        true
                                    )
                                }

                            }

                        }
//


                    }

                },
                scaffoldState = scaffoldState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .statusBarsPadding(), drawerContent = {

                    DrawerContent(gotoFav = {
                        navcontroller.navigate(NavConst.GotoFav)
                    })
                },
                topBar = {
                    SearchFun {
                        scope.launch {

                            scaffoldState.drawerState.open()
                        }
                    }
                },
                content = { padding ->


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
                                modifier = Modifier
                                    .alpha(0.7f)
                                    .clickable {
                                        musicListViewModel.setSortKey()
                                    },
                                fontSize = 14.sp
                            )

                            Box {


                                Row(verticalAlignment = CenterVertically) {


                                    IconButton(onClick = {

                                        val start = 0
                                        val end = songs.value.size
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

                        } else {
                            val lazyliststate = rememberLazyListState()
                            val imagesList: List<String> = il.list.map { detail ->
                                detail.imageUrl
                            }

//                            GlideLazyListPreloader(
//                                state = lazyliststate,
//                                data = imagesList,
//                                size = Size(Target.SIZE_ORIGINAL.toFloat(),Target.SIZE_ORIGINAL.toFloat()),
//                                numberOfItemsToPreload =50 ,
//                                requestBuilderTransform = {item, requestBuilder ->
//                                    requestBuilder.load(item)
//                                        .error(R.drawable.error_placeholder)
//                                        .override(128)
//                                }
//                            )
                            LazyColumn(state = lazyliststate) {

                                items(il.list) {

                                    MusicItem(
                                        song = it,
                                        padding,
                                        onclick = {
                                            musicListViewModel.playOrToggleTheSong(
                                                it,
                                                false
                                            )
                                        })
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }

                    }
                }, backgroundColor = MaterialTheme.colorScheme.background,
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerScrimColor = MaterialTheme.colorScheme.primary,
                drawerGesturesEnabled = !scaffoldState.drawerState.isClosed

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
fun DrawerContent(gotoFav: () -> Unit) {
    val context = LocalContext.current


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(25.dp))
        val viewmodel = viewModel<SignInViewModel>()
        val state = viewmodel.state.collectAsState()


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = CenterVertically
        ) {

            Text(text = "Melo Music ")


        }
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    gotoFav()
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = CenterVertically
        ) {
            Spacer(modifier = Modifier.width(25.dp))
            Icon(
                painter = painterResource(id = R.drawable.favorite),
                contentDescription = "Favorite"
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Favorites", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)

        }
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = CenterVertically
        ) {
            Spacer(modifier = Modifier.width(25.dp))
            Icon(imageVector = Icons.Rounded.Settings, contentDescription = "")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Settings", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)


        }
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = CenterVertically
        ) {
            Spacer(modifier = Modifier.width(25.dp))
            Icon(imageVector = Icons.Rounded.Info, contentDescription = "")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "About", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)


        }


    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserScreen(
    signIn: () -> Unit
) {
    val user = Firebase.auth.currentUser

    val vM = viewModel<SignInViewModel>()
    val isSingIn = vM.isSingingIn.collectAsState()
    if (user != null) {
        Column {


            GlideImage(model = user.photoUrl, contentDescription = "") {
                it.load(user.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            }
            Text(text = user.displayName.toString())

        }
    } else {
        var show by remember {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {


            AnimatedVisibility(visible = !isSingIn.value) {


                Button(onClick = {
                    signIn()
                }) {
                    Text(text = "SignIn")
                }
            }
            if (isSingIn.value) {
                LinearProgressIndicator()
            }
        }
    }
}

val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/bharath914"))
fun gotoMyGitHub(context: Context) {
    context.startActivity(intent)

}

@OptIn(
    ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun BottomMusicIndicator(
    media: MediaMetadataCompat,
    openDrawer: () -> Unit,
    onclickIcon: (MediaMetadataCompat) -> Unit,
) {
    val musicListViewModel: MusicListViewModel = hiltViewModel()
    val playbackstate = musicListViewModel.playbackstate.collectAsState()
    val extradetail = musicListViewModel.detailsOfTheSong.collectAsState()
    val currentduration = musicListViewModel.currDurationOfTheSong.collectAsState()

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
    Column(modifier = Modifier.fillMaxSize()) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(start = 15.dp),
            verticalAlignment = CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        Color.Black,
                        shape = RoundedCornerShape(15)
                    )
                    .size(48.dp)
            ) {


                GlideImage(
                    model = media.description.iconUri,
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15))
                        .clickable { openDrawer() }
                ) {
                    it.load(media.description.iconUri)
                        .error(R.drawable.error_placeholder)
                        .override(400)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)


                }
            }
            Spacer(modifier = Modifier
                .width(20.dp)
                .clickable { openDrawer() })
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {

                Text(
                    text = media.description.title.toString(), maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier
                        .weight(0.8f)
                        .clickable { openDrawer() }
                        .basicMarquee()
                )
                IconButton(onClick = {
                    onclickIcon(media)
                }) {
                    Icon(painter = painterResource(id = icon.value), contentDescription = "")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Slider(
            value = currentduration.value.toFloat(),
            onValueChange = {

            },
            thumb = {},
            valueRange = 0f..extradetail.value.duration.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp), colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.onSurface,
                inactiveTrackColor = MaterialTheme.colorScheme.surface
            )
        )
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
                    .priority(Priority.LOW)


                    .error(R.drawable.error_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .dontTransform()

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
                text = song.artist,
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