package com.example.fullproject.model.dirpack.database

import com.example.fullproject.model.dirpack.entities.Directory
import kotlinx.coroutines.flow.Flow

interface DbDirRepository {

    suspend fun updateDirName(id: Long, newDirName: String)

    suspend fun getDirList(onlyActive: Boolean): Flow<List<Directory>>

    suspend fun setReadFlag(id: Long, readFlag: Boolean)

    suspend fun createDirObject(uri: String, name: String?, addToStackPlaying: Boolean?, isPrimaryDir: Boolean?)

    suspend fun deleteDirObject(id: Long)

}