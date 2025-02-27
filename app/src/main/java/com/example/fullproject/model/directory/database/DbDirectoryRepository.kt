package com.example.fullproject.model.directory.database

import com.example.fullproject.model.directory.entities.InputDirectoryData
import com.example.fullproject.model.directory.entities.Directory
import kotlinx.coroutines.flow.Flow

interface DbDirectoryRepository {
    fun getAllDirectories(): Flow<List<Directory?>>

    suspend fun deleteDirectory(id: Long)

    suspend fun addDirectory(inputDirectoryData: InputDirectoryData)

    suspend fun updateValueForDirectory(directory: Directory)
}