package com.example.fullproject.di

import android.content.Context
import androidx.room.Room
import com.example.fullproject.model.database.AppDatabase
import com.example.fullproject.model.database.DatabaseConfig
import com.example.fullproject.model.directory.database.DirectoryDao
import com.example.fullproject.model.song.database.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DatabaseConfig.DATABASE_NAME)
            .createFromAsset("db_base.db")
            .build()
    }

    @Provides
    fun provideSongDao(databaseNew: AppDatabase) : SongDao {
        return databaseNew.songDao()
    }

    @Provides
    fun provideDirectoryDao(databaseNew: AppDatabase): DirectoryDao {
        return databaseNew.directoryDao()
    }
}