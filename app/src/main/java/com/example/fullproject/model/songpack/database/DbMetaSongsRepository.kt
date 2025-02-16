package com.example.fullproject.model.songpack.database

import com.example.fullproject.model.songpack.entities.MetaDataSong
import kotlinx.coroutines.flow.Flow

interface DbMetaSongsRepository {

    fun getSongs(onlyActive: Boolean): Flow<List<MetaDataSong>>

    suspend fun updateSongUserName(id: Long, newSongName: String)

    suspend fun setAutoPlayFlag(id: Long, autoPlayFlag: Boolean)

    suspend fun createSongObject(uri: String, name: String?, author: String?,
                                 description: String?, autoPlayFlag: Boolean)

    suspend fun deleteSongObject(id: Long)

    suspend fun findSongIdByURI(uri: String): Long
}