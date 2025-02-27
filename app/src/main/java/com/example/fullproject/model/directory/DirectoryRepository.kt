package com.example.fullproject.model.directory

import android.util.Log
import com.example.fullproject.model.directory.database.DbDirectoryRepository
import com.example.fullproject.model.directory.entities.InputDirectoryData
import com.example.fullproject.model.directory.entities.Directory
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

    private val _listOfAllDirectories = MutableStateFlow<List<Directory>?>(null)

    private val _listOfActiveDirectories = MutableStateFlow<List<Directory>?>(null)

    private val _directoryState: MutableStateFlow<DirectoryDbState> = MutableStateFlow(DirectoryDbState.Loading)

    private val _activeDirectoryState: MutableStateFlow<DirectoryDbState> = MutableStateFlow(DirectoryDbState.Loading)

    init {
        customScope.launch {
            dbDirectoryRepository.getAllDirectories().collect{
                list ->
                val mappedListAllDirectories = list.mapNotNull {
                    it
                }

                if (mappedListAllDirectories.isNotEmpty()){
                    _directoryState.value = DirectoryDbState.Success(mappedListAllDirectories)

                    _listOfAllDirectories.value = mappedListAllDirectories
                    val filteredActiveDirectories = mappedListAllDirectories.filter { directory -> !directory.disEnableForReading }
                    if (filteredActiveDirectories != (_activeDirectoryState.value as? DirectoryDbState.Success)?.directories)
                        _activeDirectoryState.value =  DirectoryDbState.Success(filteredActiveDirectories)
                } else {
                    _directoryState.value = DirectoryDbState.Empty
                    _activeDirectoryState.value = DirectoryDbState.Empty
                }
//
//                _listOfAllDirectories.value = mappedListAllDirectories
//                Log.d("debug 22333"," in  DirectoryRepository${mappedListAllDirectories.filter { directory -> !directory.disEnableForReading }}")
//                val filteredActiveDirectories = mappedListAllDirectories.filter { directory -> !directory.disEnableForReading }
//                if (filteredActiveDirectories != _listOfActiveDirectories.value)
//                    _listOfActiveDirectories.value = filteredActiveDirectories
            }
        }
    }

    fun getListAllDirectory(): Flow<List<Directory>?> = _listOfAllDirectories

    fun getListOfActiveDirectory(): Flow<List<Directory>?> = _listOfActiveDirectories

    fun getDirectoriesState() : Flow<DirectoryDbState> = _directoryState

    fun getActiveDirectoriesState() : Flow<DirectoryDbState> = _activeDirectoryState

    suspend fun addDirectory(inputDirectoryData: InputDirectoryData){
        dbDirectoryRepository.addDirectory(inputDirectoryData)
    }

    suspend fun deleteDirectory(id: Long){
        dbDirectoryRepository.deleteDirectory(id)
    }

    suspend fun updateDirectory(directory: Directory){
        dbDirectoryRepository.updateValueForDirectory(directory)
    }

    sealed class DirectoryDbState{
        data object Loading : DirectoryDbState()
        data class Success(val directories: List<Directory>): DirectoryDbState()
        data object Empty: DirectoryDbState()
    }
}