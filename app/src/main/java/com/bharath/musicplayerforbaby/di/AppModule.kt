package com.bharath.musicplayerforbaby.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bharath.musicplayerforbaby.data.LocalAudioInDetail
import com.bharath.musicplayerforbaby.data.MusicRepository
import com.bharath.musicplayerforbaby.data.databases.DataRepo
import com.bharath.musicplayerforbaby.data.databases.DataRepoInterface
import com.bharath.musicplayerforbaby.data.databases.RoomDB
import com.bharath.musicplayerforbaby.exoplayer.LocalMusicSource
import com.bharath.musicplayerforbaby.exoplayer.MusicServiceConnection
import com.bharath.musicplayerforbaby.other.DataBaseConst
import com.bharath.musicplayerforbaby.presentation.viewmodel.MusicListViewModel
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.data.firebase.FireBaseMusicSource
import com.bharath.musicplayerforbaby.presentation.viewmodel.SignInViewModel
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
    fun provideLocalMusicSource(musicdata: MusicRepository,fireBaseMusicSource: FireBaseMusicSource) = LocalMusicSource(musicdata,fireBaseMusicSource)

    @Singleton
    @Provides
    fun provideFirebaseMusicSource()= FireBaseMusicSource()


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

    @Singleton
    @Provides
    fun provideSignInViewModel()= SignInViewModel()

}