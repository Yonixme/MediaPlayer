package com.example.fullproject.screens.dblists

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.model.directory.DirectoryRepository
import com.example.fullproject.model.directory.entities.Directory
import com.example.fullproject.model.directory.entities.InputDirectoryData
import com.example.fullproject.model.song.MusicRepository
import com.example.fullproject.model.song.entities.SongData
import com.example.fullproject.model.song.entities.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataBaseListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val directoryRepository: DirectoryRepository
) : ViewModel() {

    private val _listSavedSongs = MutableLiveData<ReadingSongDbState>(ReadingSongDbState.Loading)
    val listSavedSongs: LiveData<ReadingSongDbState> get() = _listSavedSongs

    private val _listDirectories = MutableLiveData<ReadingDirectoryDbState>(ReadingDirectoryDbState.Loading)
    val listDirectories: LiveData<ReadingDirectoryDbState> get() = _listDirectories

    private val _songNotSavedYet = mutableListOf<Song>()
    val songNotSavedYet get() = _songNotSavedYet.toList()

    init {
        viewModelScope.launch {
            launch {
                directoryRepository.getDirectoriesState()
                    .collect{state ->
                        _listDirectories.value = when(state){
                            is DirectoryRepository.DirectoryDbState.Loading -> {
                                ReadingDirectoryDbState.Loading
                            }
                            is DirectoryRepository.DirectoryDbState.Empty -> {
                                ReadingDirectoryDbState.Empty
                            }
                            is DirectoryRepository.DirectoryDbState.Success -> {
                                ReadingDirectoryDbState.Success(state.directories)
                            }
                        }
                    }
            }

//            launch {
//                musicRepository.getListSavedSongs()
//                    .collect{ listSongsFromDb ->
//                        _listSavedSongs.value = if (listSongsFromDb != null){
//                            ReadingSongDbState.Success(listSongsFromDb)
//                        }else{
//                            ReadingSongDbState.Empty
//                        }
//                    }
//            }
//
//            launch {
//                musicRepository.getListSongsNotSavedYet().collect{newSongs ->
//                    if (_songNotSavedYet == newSongs) return@collect
//                    println("debug 22333 viewmodel ${newSongs}")
//                    _songNotSavedYet.clear()
//                    _songNotSavedYet.addAll(newSongs ?: listOf())
//                }
//            }
            launch {
                musicRepository.getListSavedSongs().collect{state->
                    _listSavedSongs.value = when(state){
                        is MusicRepository.SongDbState.Empty -> ReadingSongDbState.Empty
                        is MusicRepository.SongDbState.Loading -> ReadingSongDbState.Loading
                        is MusicRepository.SongDbState.Success -> ReadingSongDbState.Success(state.songs)
                    }
                }
            }

            launch {
                musicRepository.getListSongsNotSavedYet().collect{state ->
                    when(state){
                        is MusicRepository.SongDbState.Empty -> _songNotSavedYet.clear()
                        is MusicRepository.SongDbState.Loading -> _songNotSavedYet.clear()
                        is MusicRepository.SongDbState.Success -> {
                            if (_songNotSavedYet == state) return@collect
                            _songNotSavedYet.clear()
                            _songNotSavedYet.addAll(state.songs)
                        }
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
                ?: return@launch

            directoryRepository.updateDirectory(updatedDirectory)
        }
    }

    private fun findDirectoryByURI(uri: String): Directory? {
        return (_listDirectories.value as? ReadingDirectoryDbState.Success)
            ?.listDirectories
            ?.firstOrNull { it.uri == uri }
    }

    private fun findSongByURI(uri: String): Song? {
        return (_listSavedSongs.value as? ReadingSongDbState.Success)
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
        data class Success(val listSong: List<Song>): ReadingSongDbState()
        data class Error(val massage: String) : ReadingSongDbState()
        data object Empty: ReadingSongDbState()
    }

    sealed class ReadingDirectoryDbState{
        data object Loading : ReadingDirectoryDbState()
        data class Success(val listDirectories: List<Directory>): ReadingDirectoryDbState()
        data class Error(val massage: String) : ReadingDirectoryDbState()
        data object Empty: ReadingDirectoryDbState()
    }
}