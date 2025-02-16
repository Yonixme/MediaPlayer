package com.example.fullproject.model.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fullproject.model.room.directory.database.DirectoryDao
import com.example.fullproject.model.room.directory.database.entities.DirectoryDbEntity
import com.example.fullproject.model.room.song.database.SongDao
import com.example.fullproject.model.room.song.database.entities.SongDbEntity


@Database(entities = [
    SongDbEntity::class,
    DirectoryDbEntity::class],
    version = 1,
    )
abstract class AppDatabaseNew : RoomDatabase() {
    abstract fun songDao(): SongDao

    abstract fun directoryDao(): DirectoryDao
}

object DatabaseConfig {
    const val DATABASE_NAME = "db"
}
