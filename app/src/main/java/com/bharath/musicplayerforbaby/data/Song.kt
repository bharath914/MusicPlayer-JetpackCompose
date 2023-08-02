package com.bharath.musicplayerforbaby.data

import android.net.Uri

data class Song (
    val mediaId :String = "",
    val title:String ="",
    val subtitle:String ="",
    val songUrl :String ="",
    val imageUrl :String="",
    val duration :Long = 0L
){
    fun deosMatchSearchQuery(query:String) :Boolean{
        val mathcingCombinations  = listOf(
            "$title$subtitle",
            "$title $subtitle",
            "${title.first()} ${subtitle.first()}"
        )
        return mathcingCombinations.any{
            it.contains(query,true)
        }
    }
}
