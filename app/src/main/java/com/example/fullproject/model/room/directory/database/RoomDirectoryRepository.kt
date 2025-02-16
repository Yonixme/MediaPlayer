package com.example.fullproject.model.room.directory.database

import com.example.fullproject.model.room.directory.database.entities.DirectoryDbEntity
import com.example.fullproject.model.room.directory.entities.InputDirectoryData
import com.example.fullproject.model.room.directory.entities.DirectoryNew
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDirectoryRepository @Inject constructor(
    private val directoryDao: DirectoryDao
) : DbDirectoryRepository {
    override fun getAllDirectories(): Flow<List<DirectoryNew?>> {
        return directoryDao.getAllDirectories()
            .map { list ->
                list.map {
                    it?.toDirectory()
                }
            }
    }

    override suspend fun deleteDirectory(id: Long) {
        directoryDao.deleteDirectory(id)
    }

    override suspend fun addDirectory(inputDirectoryData: InputDirectoryData) {
        directoryDao.insertDirectory(DirectoryDbEntity.fromInputDirectoryData(inputDirectoryData))
    }

    override suspend fun updateValueForDirectory(directory: DirectoryNew) {
        directoryDao.updateValueForDirectory(DirectoryDbEntity.fromDirectory(directory))
    }
}