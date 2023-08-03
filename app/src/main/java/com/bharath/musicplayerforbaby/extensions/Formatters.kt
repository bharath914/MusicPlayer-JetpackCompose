package com.bharath.musicplayerforbaby.extensions

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale


fun convertToTime(l: Long): String {
    val formatStr = SimpleDateFormat("mm:ss", Locale.getDefault())
    val str = formatStr.format(l)
    return str
}

fun convertMillisecondsToMinutesAndSeconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun convertToKbps(bits: String): String {
    val nbits = if (bits.isNotEmpty()) bits.toLong() else 0
    val kilobits = nbits / 1024
    return "$kilobits Kbps"
}

fun formatMimeType(str: String): String {
    var s = "n/a"
    if (str.isNotEmpty()) {
        s = if (str.contains("audio/")) {
            str.substringAfter("audio/")
        } else {
            str
        }
    }

    return s.uppercase()
}

fun formatSize(bit: String): String {


    if (bit.isNotEmpty()) {
//            8 000 000
        Log.d("SongSize", "size: $bit")
        val bits = bit.toFloat()
        val megabytes = bits / 1000000f
        val formateed = "%.02f".format(megabytes)
        return "${formateed}MB"
    }
    return ""
}
