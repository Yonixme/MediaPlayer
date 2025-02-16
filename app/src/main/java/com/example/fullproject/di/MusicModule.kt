package com.example.fullproject.di

import com.example.fullproject.model.room.song.controller.MediaPlayerMusicController
import com.example.fullproject.model.room.song.controller.MusicController
import com.example.fullproject.model.room.song.infoprovider.MusicInfoProvider
import com.example.fullproject.model.room.song.infoprovider.MediaPlayerInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MusicModule {

    @Binds
    abstract fun bindMusicController(mediaPlayerMusicController: MediaPlayerMusicController): MusicController

    @Binds
    abstract fun bindMusicInfoProvider(mediaPlayerInfoProvider: MediaPlayerInfoProvider): MusicInfoProvider
}