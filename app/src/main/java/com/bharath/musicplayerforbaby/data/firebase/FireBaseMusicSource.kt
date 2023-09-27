package com.bharath.musicplayerforbaby.data.firebase

import com.bharath.musicplayerforbaby.data.DetailSong
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FireBaseMusicSource {
    val firebase = FirebaseFirestore.getInstance()
    val musicCOllection = firebase.collection("songsList")

    suspend fun getMusicCollection() = try {
        musicCOllection.get().await().toObjects(DetailSong::class.java)

    } catch (e: Exception) {
        e.printStackTrace()
        emptyList<DetailSong>()
    }

}