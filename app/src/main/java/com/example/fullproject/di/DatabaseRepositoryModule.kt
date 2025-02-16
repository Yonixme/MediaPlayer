package com.example.fullproject.di

import com.example.fullproject.model.dirpack.database.DbDirRepository
import com.example.fullproject.model.dirpack.database.SQLiteDirRepository
import com.example.fullproject.model.room.directory.database.DbDirectoryRepository
import com.example.fullproject.model.room.directory.database.RoomDirectoryRepository
import com.example.fullproject.model.room.song.database.DbSongRepository
import com.example.fullproject.model.room.song.database.RoomSongRepository
import com.example.fullproject.model.songpack.database.DbMetaSongsRepository
import com.example.fullproject.model.songpack.database.SQLiteMetaSongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseRepositoryModule {

    @Binds
    abstract fun bindDbMetaSongsRepository(sqLiteMetaSongRepository: SQLiteMetaSongRepository) : DbMetaSongsRepository

    @Binds
    abstract fun bindDbDirRepository(sqLiteDirRepository: SQLiteDirRepository) : DbDirRepository


    @Binds
    abstract fun bindDbSongRepository(roomSongRepository: RoomSongRepository) : DbSongRepository

    @Binds
    abstract fun bindDbDirectoryRepository(roomDirectoryRepository: RoomDirectoryRepository): DbDirectoryRepository
}