package com.example.fullproject.model.sqlite.songpack

import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong
import kotlinx.coroutines.flow.Flow

interface MetaSongsRepository {
    fun getSongs(onlyActive: Boolean): Flow<List<MetaDataSong>>

    suspend fun updateSongUserName(id: Long, newSongName: String)

    suspend fun updateFlagItem(id: Long, activeState: Boolean)

    suspend fun createSongObject(uri: String, name: String?, author: String?,
                                 description: String?, addToStackPlaying: Boolean)

    suspend fun deleteSongObject(id: Long)

    suspend fun findSongIdByURI(uri: String): Long
}