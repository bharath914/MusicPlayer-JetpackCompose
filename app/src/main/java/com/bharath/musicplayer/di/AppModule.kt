package com.bharath.musicplayer.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bharath.musicplayer.data.LocalAudioInDetail
import com.bharath.musicplayer.data.MusicRepository
import com.bharath.musicplayer.data.databases.DataRepo
import com.bharath.musicplayer.data.databases.DataRepoInterface
import com.bharath.musicplayer.data.databases.RoomDB
import com.bharath.musicplayer.exoplayer.LocalMusicSource
import com.bharath.musicplayer.exoplayer.MusicServiceConnection
import com.bharath.musicplayer.other.DataBaseConst
import com.bharath.musicplayer.presentation.MusicListViewModel
import com.bharath.musicplayerforbaby.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideMusicServiceConnection(@ApplicationContext context: Context) =
        MusicServiceConnection(context)

    @Provides
    @Singleton
    fun provideRoomDataBase(app: Application) = Room.databaseBuilder(
        app,
        RoomDB::class.java,
        DataBaseConst.DATABASE_NAME
    ).build()


    @Provides
    @Singleton
    fun provideDatabaseRepository(db: RoomDB): DataRepoInterface = DataRepo(db.dao)


    @Singleton
    @Provides
    fun provideLocalMusicSource(musicdata: MusicRepository) = LocalMusicSource(musicdata)


    @Provides
    @Singleton
    fun provideMusicListViewModel(
        musicServiceConnection: MusicServiceConnection,
        localMusicSource: LocalMusicSource,
        dataRepoInterface: DataRepoInterface,
    ) = MusicListViewModel(
        musicServiceConnection = musicServiceConnection,
        localMusicSource,
        dataRepoInterface
    )

    @Singleton
    @Provides
    fun provideGlideInstance(@ApplicationContext context: Context) =
        Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        )

    @Provides
    @Singleton
    fun provideLocalAudioInDetail(@ApplicationContext context: Context) =
        LocalAudioInDetail(context)

}