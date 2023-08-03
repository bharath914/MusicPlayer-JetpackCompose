package com.bharath.musicplayerforbaby.di

//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

import android.content.Context
import com.bharath.musicplayerforbaby.data.LocalAudio
import com.bharath.musicplayerforbaby.data.LocalAudioInDetail
import com.bharath.musicplayerforbaby.data.MusicRepository
import com.bharath.musicplayerforbaby.exoplayer.LocalMusicSource
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

//@Module
//@InstallIn(ServiceComponent::class)
//object ServiceModule {
//
//    @Provides
//    @ServiceScoped
//    fun provideRepository(localAudio: LocalAudio) = MusicRepository(localAudio)
//
//
//    @Provides
//    @ServiceScoped
//    fun provideLocalAudio(@ApplicationContext context: Context) = LocalAudio(context)
//
//    @Provides
//    @ServiceScoped
//    fun provideAudioAttributes() = AudioAttributes.Builder()
//        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
//        .setUsage(C.USAGE_MEDIA)
//        .build()
//
//    @Provides
//    @ServiceScoped
//    fun provideExoplayer(@ApplicationContext context: Context, audioAttributes: AudioAttributes) =
//        SimpleExoPlayer.Builder(context).build().apply {
//            setAudioAttributes(audioAttributes, true)
//            setHandleAudioBecomingNoisy(true)
//
//        }
//
//
//
//
//    @Provides
//    @ServiceScoped
//    fun provideDataSourceFactory(
//        @ApplicationContext context: Context,
//
//        )= DefaultDataSourceFactory(context, Util.getUserAgent(context,"MusicAppForBaby"))
//
//}

//package com.bharath.musicplayerpl.di
//
//import android.content.Context
//import com.bharath.musicplayerpl.data.remote.MusicDataBase
//import com.bharath.musicplayerpl.ext.LocalAudio
//import com.google.android.exoplayer2.C
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.audio.AudioAttributes
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ServiceComponent
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.android.scopes.ServiceScoped
//

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideMusicDatabase(localAudio: LocalAudio) = MusicRepository(localAudio)

    @Provides
    @ServiceScoped
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @ServiceScoped
    fun provideLocalAudio(@ApplicationContext context: Context) =LocalAudio(context)


    @Provides
    @ServiceScoped
    fun provideExoplayer(@ApplicationContext context: Context, audioAttributes: AudioAttributes) =
        SimpleExoPlayer.Builder(context).build().apply {
            setAudioAttributes(audioAttributes, true)
            setHandleAudioBecomingNoisy(true)


        }

    @Provides
    @ServiceScoped
    fun provideDataSourceFactory(
        @ApplicationContext context: Context,

        )= DefaultDataSourceFactory(context,Util.getUserAgent(context,"MusicApp"))


}

