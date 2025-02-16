package com.example.fullproject.screens.dblists.newr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.model.room.directory.controller.DirectoryRepository
import com.example.fullproject.model.room.directory.entities.DirectoryNew
import com.example.fullproject.model.room.directory.entities.InputDirectoryData
import com.example.fullproject.model.room.song.MusicRepository
import com.example.fullproject.model.room.song.entities.SongData
import com.example.fullproject.model.room.song.entities.SongNew
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataBaseListViewModelNew @Inject constructor(
    private val musicRepository: MusicRepository,
    private val directoryRepository: DirectoryRepository
) : ViewModel() {

    private val _listSongs = MutableLiveData<ReadingSongDbState>(ReadingSongDbState.Loading)
    val listSongs: LiveData<ReadingSongDbState> get() = _listSongs

    private val _listDirectories = MutableLiveData<ReadingDirectoryDbState>(ReadingDirectoryDbState.Loading)
    val listDirectories: LiveData<ReadingDirectoryDbState> get() = _listDirectories

    init {
        viewModelScope.launch {
            launch {
                directoryRepository.getListAllDirectory()
                    .collect{ listDirectoriesFromDb ->
                        _listDirectories.value = if (listDirectoriesFromDb != null){
                            ReadingDirectoryDbState.Success(listDirectoriesFromDb)
                        }else{
                            ReadingDirectoryDbState.Empty
                        }
                    }
            }

            launch {
                musicRepository.getListSavedSongs()
                    .collect{ listSongsFromDb ->
                        _listSongs.value = if (listSongsFromDb != null){
                            println("Song123 in songs flow block")
                            ReadingSongDbState.Success(listSongsFromDb)
                        }else{
                            println("Song123 in songs is empty")
                            ReadingSongDbState.Empty
                        }
                    }
            }
        }
    }

    fun updateFlagAutoPlaySong(uri: String, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedSong = findSongByURI(uri)?.copy(disEnableAutoPlay = isChecked) ?: return@launch

            if (updatedSong.id < 0){
                writeSongInDB(
                    uri = updatedSong.uri,
                    name = updatedSong.name,
                    author = updatedSong.author,
                    disEnableAutoPlay = updatedSong.disEnableAutoPlay
                )
            } else {
                musicRepository.updateSongInDb(updatedSong)
            }
        }
    }

    fun updateFlagAddPlaylistDir(uri: String, isChecked: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedDirectory = findDirectoryByURI(uri)?.copy(disEnableForReading = isChecked)
            if (updatedDirectory == null) return@launch

            directoryRepository.updateDirectory(updatedDirectory)
        }
    }

    private fun findDirectoryByURI(uri: String): DirectoryNew? {
        return (_listDirectories.value as? ReadingDirectoryDbState.Success)
            ?.listDirectories
            ?.firstOrNull { it.uri == uri }
    }

    private fun findSongByURI(uri: String): SongNew? {
        return (_listSongs.value as? ReadingSongDbState.Success)
            ?.listSong
            ?.firstOrNull { it.uri == uri }
    }

    fun deleteSongElement(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.deleteSongInDb(id)
        }
    }

    fun deleteDirElement(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            directoryRepository.deleteDirectory(id)
        }
    }

    fun writeDirectoryInDB(uri: String,
                     name: String?,
                     disEnableForReading: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            directoryRepository.addDirectory(
                InputDirectoryData(
                uri = uri,
                name = name,
                disEnableForReading = disEnableForReading
                )
            )
        }
    }

    fun writeSongInDB(uri: String,
                      name: String?,
                      author: String?,
                      disEnableAutoPlay: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.addSongInDb(
                SongData(
                    uri = uri,
                    name = name,
                    author = author,
                    disEnableAutoPlay = disEnableAutoPlay)
            )
        }
    }

    sealed class ReadingSongDbState{
        data object Loading : ReadingSongDbState()
        data class Success(val listSong: List<SongNew>): ReadingSongDbState()
        data class Error(val massage: String) : ReadingSongDbState()
        data object Empty: ReadingSongDbState()
    }

    sealed class ReadingDirectoryDbState{
        data object Loading : ReadingDirectoryDbState()
        data class Success(val listDirectories: List<DirectoryNew>): ReadingDirectoryDbState()
        data class Error(val massage: String) : ReadingDirectoryDbState()
        data object Empty: ReadingDirectoryDbState()
    }
}