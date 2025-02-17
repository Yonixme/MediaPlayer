package com.example.fullproject.di

import com.example.fullproject.model.services.MusicServiceManager
import com.example.fullproject.model.services.MusicServiceManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceManagerModule {

    @Binds
    abstract fun bindMusicServiceManager(musicServiceManagerImpl: MusicServiceManagerImpl) : MusicServiceManager
}