package com.example.fullproject.model.room.song.database

import com.example.fullproject.model.room.song.entities.SongData
import com.example.fullproject.model.room.song.entities.SongNew
import kotlinx.coroutines.flow.Flow

interface DbSongRepository {
    fun getAllSongs(): Flow<List<SongNew?>>

    suspend fun deleteSong(id: Long)

    suspend fun addSong(songData: SongData)

    suspend fun updateValueForSong(songNew: SongNew)

    suspend fun findByURI(uri: String): SongNew?
}