package com.example.fullproject.di

import com.example.fullproject.model.room.song.SongSource
import com.example.fullproject.sources.local.MediaSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourceModule{

    @Binds
    abstract fun bindSymbolPriceSource(mediaSource: MediaSource): SongSource
}