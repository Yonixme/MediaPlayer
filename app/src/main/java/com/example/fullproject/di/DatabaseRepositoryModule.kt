package com.example.fullproject.di

import com.example.fullproject.model.directory.database.DbDirectoryRepository
import com.example.fullproject.model.directory.database.RoomDirectoryRepository
import com.example.fullproject.model.song.database.DbSongRepository
import com.example.fullproject.model.song.database.RoomSongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseRepositoryModule {
    @Binds
    abstract fun bindDbSongRepository(roomSongRepository: RoomSongRepository) : DbSongRepository

    @Binds
    abstract fun bindDbDirectoryRepository(roomDirectoryRepository: RoomDirectoryRepository): DbDirectoryRepository
}