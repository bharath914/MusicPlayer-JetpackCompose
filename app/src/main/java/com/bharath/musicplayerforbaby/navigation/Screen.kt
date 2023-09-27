package com.bharath.musicplayerforbaby.navigation

sealed class Screen(val route:String,val name :String) {
    object favourites :Screen(NavConst.GotoFav,"Favorites")
    object home :Screen(NavConst.Home,"Home")
}
