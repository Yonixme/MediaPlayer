package com.example.fullproject.services.model.songpack

import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    suspend fun getSongs(onlyActive: Boolean): Flow<List<MetaDataSong>>

    suspend fun updateSongUserName(id: Long, newSongName: String)

    suspend fun updateFlagItem(id: Long, activeState: Boolean)

    suspend fun createSongObject(uri: String, name: String?, author: String?,
                                 description: String?, addToStackPlaying: Boolean)

    suspend fun deleteSongObject(id: Long): Int

    suspend fun findSongIdByURI(uri: String): Long
}