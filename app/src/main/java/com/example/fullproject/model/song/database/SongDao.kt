package com.example.fullproject.model.song.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fullproject.model.song.database.entities.SongDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert
    suspend fun insertSong(song: SongDbEntity)

    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<SongDbEntity?>>

    @Query("SELECT * FROM songs WHERE uri = :uri")
    suspend fun findByURI(uri: String): SongDbEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateValueForSong(song: SongDbEntity)

    @Query("DELETE FROM songs WHERE id = :id")
    fun deleteSong(id: Long)
}