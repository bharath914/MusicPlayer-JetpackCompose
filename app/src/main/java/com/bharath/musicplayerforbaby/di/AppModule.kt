package com.bharath.musicplayerforbaby.di

import android.content.Context
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.data.LocalAudio
import com.bharath.musicplayerforbaby.data.LocalAudioInDetail
import com.bharath.musicplayerforbaby.data.MusicRepository
import com.bharath.musicplayerforbaby.exoplayer.LocalMusicSource
import com.bharath.musicplayerforbaby.exoplayer.MusicServiceConnection
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule
{
    @Singleton
    @Provides
    fun provideMusicServiceConnection(@ApplicationContext context: Context) = MusicServiceConnection(context)
    @Singleton
    @Provides
    fun provideLocalMusicSource(musicdata:LocalAudioInDetail) = LocalMusicSource(musicdata)
    @Provides
    @Singleton
    fun provideLocalAudio(@ApplicationContext context: Context) =LocalAudio(context)

    @Singleton
    @Provides
    fun provideGlideInstance(@ApplicationContext context: Context)= Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

    )

    @Provides
    @Singleton
    fun provideLocalAudioInDetail(@ApplicationContext context: Context) = LocalAudioInDetail(context)

}