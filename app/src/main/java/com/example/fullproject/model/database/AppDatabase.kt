package com.example.fullproject.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fullproject.model.directory.database.DirectoryDao
import com.example.fullproject.model.directory.database.entities.DirectoryDbEntity
import com.example.fullproject.model.song.database.SongDao
import com.example.fullproject.model.song.database.entities.SongDbEntity


@Database(entities = [
    SongDbEntity::class,
    DirectoryDbEntity::class],
    version = 1,
    )
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    abstract fun directoryDao(): DirectoryDao
}

object DatabaseConfig {
    const val DATABASE_NAME = "db"
}
