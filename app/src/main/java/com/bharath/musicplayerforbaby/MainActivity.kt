package com.bharath.musicplayerforbaby

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.bharath.musicplayerforbaby.navigation.MyNavHostContainer
import com.bharath.musicplayerforbaby.presentation.viewmodel.MusicListViewModel
import com.bharath.musicplayerforbaby.ui.theme.MusicPlayerForBabyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var musicListViewModel: MusicListViewModel

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition(condition = {
            !musicListViewModel.showUi.value
        })
        setContent {
            MusicPlayerForBabyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val permission = rememberPermissionState(
                        permission = if (Build.VERSION.SDK_INT > 32) {
                            Manifest.permission.READ_MEDIA_AUDIO
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                    )
                    val lifecylerowner = LocalLifecycleOwner.current
                    DisposableEffect(key1 = lifecylerowner) {
                        val observer = LifecycleEventObserver { _, event ->

                            if (event == Lifecycle.Event.ON_RESUME) {
                                permission.launchPermissionRequest()

                            }
                        }
                        lifecylerowner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecylerowner.lifecycle.removeObserver(observer)
                        }
                    }

                    if (!permission.status.isGranted) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Provide the Storage Permission to Display the Songs \n Setting -> Permission -> Storage",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        val navHostController = rememberNavController()

                        MyNavHostContainer(navHostController = navHostController)


                    }
                }
            }
        }
    }


    override fun onDestroy() {

        musicListViewModel.saveLastPlayedSong()
        Log.d("Stopped", "Stopped")
        super.onDestroy()
    }
}

