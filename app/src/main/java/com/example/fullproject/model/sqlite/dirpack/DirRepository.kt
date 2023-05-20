package com.example.fullproject.model.sqlite.dirpack

import com.example.fullproject.model.sqlite.dirpack.entities.Dir
import kotlinx.coroutines.flow.Flow

interface DirRepository {
    suspend fun updateDirName(id: Long, newSongName: String)

    suspend fun getDirList(onlyActive: Boolean): Flow<List<Dir>>

    suspend fun updateFlagItem(id: Long, activeState: Boolean)

    suspend fun createDirObject(uri: String, name: String?, addToStackPlaying: Boolean?, isPrimaryDir: Boolean?)

    suspend fun deleteDirObject(id: Long)
}