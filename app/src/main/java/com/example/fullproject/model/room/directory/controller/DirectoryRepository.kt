package com.example.fullproject.model.room.directory.controller

import com.example.fullproject.model.room.directory.database.DbDirectoryRepository
import com.example.fullproject.model.room.directory.entities.InputDirectoryData
import com.example.fullproject.model.room.directory.entities.DirectoryNew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectoryRepository @Inject constructor(
    private val dbDirectoryRepository: DbDirectoryRepository
) {
    private val customScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _listOfAllDirectories = MutableStateFlow<List<DirectoryNew>?>(null)

    private val _listOfActiveDirectories = MutableStateFlow<List<DirectoryNew>?>(null)

    init {
        customScope.launch {
            dbDirectoryRepository.getAllDirectories().collect{
                list ->
                val mappedListAllDirectories = list.mapNotNull {
                    it
                }

                _listOfAllDirectories.value = mappedListAllDirectories

                val filteredActiveDirectories = mappedListAllDirectories.filter { directory -> !directory.disEnableForReading }
                if (filteredActiveDirectories != _listOfActiveDirectories.value)
                    _listOfActiveDirectories.value = filteredActiveDirectories

            }
        }
    }

    fun getListAllDirectory(): Flow<List<DirectoryNew>?> = _listOfAllDirectories

    fun getListOfActiveDirectory(): Flow<List<DirectoryNew>?> = _listOfActiveDirectories

    suspend fun addDirectory(inputDirectoryData: InputDirectoryData){
        dbDirectoryRepository.addDirectory(inputDirectoryData)
    }

    suspend fun deleteDirectory(id: Long){
        dbDirectoryRepository.deleteDirectory(id)
    }

    suspend fun updateDirectory(directory: DirectoryNew){
        dbDirectoryRepository.updateValueForDirectory(directory)
    }
}