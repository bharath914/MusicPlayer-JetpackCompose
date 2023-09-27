package com.bharath.musicplayerforbaby.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bharath.musicplayerforbaby.MainActivity
import com.bharath.musicplayerforbaby.presentation.Favorites
import com.bharath.musicplayerforbaby.presentation.MusicListScreen
import com.bharath.musicplayerforbaby.presentation.signIn.SignInScreen

@Composable
fun MyNavHostContainer(
    navHostController: NavHostController,

    ) {
    NavHost(navController = navHostController, startDestination = NavConst.SignIn, builder = {
        composable(NavConst.Home){
           MusicListScreen(navcontroller = navHostController)
        }
        composable(NavConst.GotoFav){
            Favorites()
        }
        composable(NavConst.SignIn){
            SignInScreen(navHostController = navHostController)

        }

    })
}