package com.bharath.musicplayerforbaby.data

data class DetailSong (
    val mediaId :String = "",
    val title:String ="",
    val albumName :String="",
    val subtitle:String ="",
    val songUrl :String ="",
    val imageUrl :String="",
    val duration :Long = 0L,
    val bitrate : String = "",
    val size :String = "",
    val mimeType :String = "",

    val albumartist :String= "",
    val dateAdded :String= "",
    val dateModified :String= "",

        ){
    fun deosMatchSearchQuery(query:String) :Boolean{
        val mathcingCombinations  = listOf(
            "$title$subtitle",
            "$title $subtitle",
            "$albumName $title",
            "${title.first()} ${subtitle.first()}"
        )
        return mathcingCombinations.any{
            it.contains(query,true)
        }
    }
}

data class DurationAndOther(
    val duration :Long,
    val bitrate: String,
    val mimeType: String,
    val size: String,
    val samplingRate :String= "",
    val albumName :String,
    val albumArtist :String
)