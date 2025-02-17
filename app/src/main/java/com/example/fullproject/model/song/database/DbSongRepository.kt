package com.example.fullproject.model.song.database

import com.example.fullproject.model.song.entities.SongData
import com.example.fullproject.model.song.entities.Song
import kotlinx.coroutines.flow.Flow

interface DbSongRepository {
    fun getAllSongs(): Flow<List<Song?>>

    suspend fun deleteSong(id: Long)

    suspend fun addSong(songData: SongData)

    suspend fun updateValueForSong(songNew: Song)

    suspend fun findByURI(uri: String): Song?
}