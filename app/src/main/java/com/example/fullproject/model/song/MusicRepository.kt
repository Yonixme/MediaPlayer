package com.example.fullproject.model.song

import android.util.Log
import com.example.fullproject.model.directory.DirectoryRepository
import com.example.fullproject.model.directory.DirectoryRepository.DirectoryDbState
import com.example.fullproject.model.song.database.DbSongRepository
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val dbSongRepository: DbSongRepository,
    private val directoryRepository: DirectoryRepository,
    private val songSource: SongSource
){
    private val customScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _listSavedSongs: MutableStateFlow<SongDbState> = MutableStateFlow(SongDbState.Loading)
    private val _listSongsFromDevice: MutableStateFlow<SongDbState> = MutableStateFlow(SongDbState.Loading)
    private val _songsNotSavedYetNew: MutableStateFlow<SongDbState> = MutableStateFlow(SongDbState.Loading)

    init {
        customScope.launch {
            launch {
                combine(
                dbSongRepository.getAllSongs(),
                directoryRepository.getActiveDirectoriesState()
                ) {songFromDb, activeDirectories ->
                    val mappedListOfMusic = songFromDb.mapNotNull { it }
                    val savedSongsState = if (mappedListOfMusic.isNotEmpty()) {
                        SongDbState.Success(mappedListOfMusic)
                    } else {
                        SongDbState.Empty
                    }
                    if (savedSongsState !=
                        (_listSavedSongs.value as? SongDbState.Success)?.songs)
                        _listSavedSongs.value = savedSongsState

                    when(activeDirectories){
                        DirectoryDbState.Empty -> {
                            _listSongsFromDevice.value = SongDbState.Empty
                            _songsNotSavedYetNew.value = SongDbState.Empty
                        }
                        DirectoryDbState.Loading -> {
                            _listSongsFromDevice.value = SongDbState.Loading
                            _songsNotSavedYetNew.value = SongDbState.Loading
                        }
                        is DirectoryDbState.Success -> {
                            val songsFromDevice = songSource.getAudioFileFromDirectories(
                                directories = activeDirectories.directories,
                                songsFromDb = if(savedSongsState is SongDbState.Success) savedSongsState.songs else emptyList()
                            )

                            val correctSongsList = if (savedSongsState is SongDbState.Success) {
                                songsFromDevice.map { songFromDevice ->
                                    savedSongsState.songs.find { it.uri == songFromDevice.uri} ?: songFromDevice
                                }
                            } else {
                                songsFromDevice
                            }
                            _listSongsFromDevice.value = if (correctSongsList.isEmpty()) SongDbState.Empty else SongDbState.Success(correctSongsList)
                            _songsNotSavedYetNew.value = if (savedSongsState is SongDbState.Success) {
                                SongDbState.Success(correctSongsList.filterNot { song -> savedSongsState.songs.contains(song) })
                            } else {
                                SongDbState.Success(songsFromDevice)
                            }
                        }
                    }
                }.collect()
            }

            launch {
                _listSavedSongs.collect{ state ->
                    val listSongs = (_listSongsFromDevice.value as? SongDbState.Success)?.songs ?: listOf()
                    val oldSavedSongs = (_listSavedSongs.value as? SongDbState.Success)?.songs ?: listOf()
                    when(state){
                        is SongDbState.Success -> {
                            val equalLists = state.songs.map { it.uri } == oldSavedSongs.map { it.uri }
                            if (equalLists) return@collect
                            _songsNotSavedYetNew.value = SongDbState.Success(listSongs.filterNot { song ->
                                state.songs.contains(song)
                            })
                        }
                        is SongDbState.Empty -> {
                            _songsNotSavedYetNew.value = SongDbState.Success(listSongs)
                            return@collect
                        }
                        is SongDbState.Loading -> return@collect
                    }
                }
            }
        }
    }

    fun getListSavedSongs(): Flow<SongDbState> = _listSavedSongs

    fun getListSongsFromDevice(): Flow<SongDbState> = _listSongsFromDevice

    fun getListSongsNotSavedYet(): Flow<SongDbState> = _songsNotSavedYetNew


    suspend fun addSongInDb(songData: SongData){
        dbSongRepository.addSong(songData)
    }

    suspend fun deleteSongInDb(id: Long){
        dbSongRepository.deleteSong(id)
    }

    suspend fun updateSongInDb(song: Song){
        dbSongRepository.updateValueForSong(song)
    }

    fun getSongByURI(uri: String): Song?{
        return (_listSongsFromDevice.value as? SongDbState.Success)?.songs?.firstOrNull{uri == it.uri}
    }

    suspend fun refreshSongsFromDevice() {
        _listSongsFromDevice.value = SongDbState.Loading
        _songsNotSavedYetNew.value = SongDbState.Loading
        val listActiveDirectories = (directoryRepository.getActiveDirectoriesState().first() as? DirectoryDbState.Success)?.directories ?: emptyList()
        val songsFromDb = (_listSavedSongs.value as? SongDbState.Success)?.songs ?: emptyList()

        val songsFromDevice = songSource.getAudioFileFromDirectories(
            directories = listActiveDirectories,
            songsFromDb = songsFromDb
        )

        _listSongsFromDevice.value =
            if (songsFromDevice.isNotEmpty())
                SongDbState.Success(songsFromDevice)
            else
                SongDbState.Empty

        val savedSongs = _listSavedSongs.value

        _songsNotSavedYetNew.value = if (savedSongs is SongDbState.Success) {
            SongDbState.Success(songsFromDevice.filterNot { song -> savedSongs.songs.contains(song) })
        } else {
            SongDbState.Success(songsFromDevice)
        }

    }

    sealed class SongDbState{
        data object Loading : SongDbState()
        data class Success(val songs: List<Song>): SongDbState()
        data object Empty: SongDbState()
    }
}