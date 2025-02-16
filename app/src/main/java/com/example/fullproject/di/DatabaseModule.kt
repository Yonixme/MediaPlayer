package com.example.fullproject.di

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import com.example.fullproject.model.database.AppSQLiteHelper
import com.example.fullproject.model.room.database.AppDatabaseNew
import com.example.fullproject.model.room.database.DatabaseConfig
import com.example.fullproject.model.room.directory.database.DirectoryDao
import com.example.fullproject.model.room.song.database.SongDao
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
    fun provideDatabase(@ApplicationContext context: Context): SQLiteDatabase{
        return AppSQLiteHelper(context).writableDatabase
    }

    @Provides
    @Singleton
    fun provideDatabaseNew(@ApplicationContext context: Context): AppDatabaseNew {
        return Room.databaseBuilder(context, AppDatabaseNew::class.java, DatabaseConfig.DATABASE_NAME)
            .build()
    }

    @Provides
    fun provideSongDao(databaseNew: AppDatabaseNew) : SongDao{
        return databaseNew.songDao()
    }

    @Provides
    fun provideDirectoryDao(databaseNew: AppDatabaseNew): DirectoryDao{
        return databaseNew.directoryDao()
    }
}