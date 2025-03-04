package com.example.fullproject.di

import com.example.fullproject.model.song.provider.controller.MediaPlayerMusicController
import com.example.fullproject.model.song.provider.controller.MusicController
import com.example.fullproject.model.song.provider.infoprovider.MusicInfoProvider
import com.example.fullproject.model.song.provider.infoprovider.MediaPlayerInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MusicModule {
    @Binds
    abstract fun bindMusicController(mediaPlayerMusicController: MediaPlayerMusicController): MusicController

    @Binds
    abstract fun bindMusicInfoProvider(mediaPlayerInfoProvider: MediaPlayerInfoProvider): MusicInfoProvider
}