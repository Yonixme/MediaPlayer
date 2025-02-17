package com.example.fullproject.model.directory.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fullproject.model.directory.database.entities.DirectoryDbEntity
import com.example.fullproject.model.song.database.entities.SongDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDao {
    @Insert
    suspend fun insertDirectory(directoryDbEntity: DirectoryDbEntity)

    @Query("SELECT * FROM directories")
    fun getAllDirectories(): Flow<List<DirectoryDbEntity?>>

    @Query("SELECT * FROM directories WHERE uri = :uri")
    suspend fun findByURI(uri: String): DirectoryDbEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateValueForDirectory(directoryDbEntity: DirectoryDbEntity)

    @Query("DELETE FROM directories WHERE id = :id")
    fun deleteDirectory(id: Long)
}