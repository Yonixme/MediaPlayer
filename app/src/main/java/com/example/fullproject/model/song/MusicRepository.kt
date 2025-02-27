package com.example.fullproject.model.song

import android.net.Uri
import android.util.Log
import com.example.fullproject.model.directory.DirectoryRepository
import com.example.fullproject.model.directory.DirectoryRepository.DirectoryDbState
import com.example.fullproject.model.directory.entities.Directory
import com.example.fullproject.model.song.database.DbSongRepository
import com.example.fullproject.model.song.entities.SongData
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongWithDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val dbSongRepository: DbSongRepository,
    private val directoryRepository: DirectoryRepository,
    private val songSource: SongSource
){
    private val customScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _listSavedSongs: MutableStateFlow<List<Song>?> = MutableStateFlow(null)
    private val _listSongsFromDevice: MutableStateFlow<List<Song>?> = MutableStateFlow(null)
    private val _autoPlayListSongs: MutableStateFlow<List<Song>?> = MutableStateFlow(null)
    private val _songsNotSavedYet: MutableStateFlow<List<Song>?> = MutableStateFlow(null)

    private val _listSavedSongsNew: MutableStateFlow<SongDbState> = MutableStateFlow(SongDbState.Loading)
    private val _listSongsFromDeviceNew: MutableStateFlow<SongDbState> = MutableStateFlow(SongDbState.Loading)
    private val _autoPlayListSongsNew: MutableStateFlow<SongDbState> = MutableStateFlow(SongDbState.Loading)
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
                    if (savedSongsState != _listSavedSongs.value) _listSavedSongsNew.value = savedSongsState

                    when(activeDirectories){
                        DirectoryDbState.Empty -> {
                            _listSongsFromDeviceNew.value = SongDbState.Empty
                            _autoPlayListSongsNew.value = SongDbState.Empty
                            _songsNotSavedYetNew.value = SongDbState.Empty
                        }
                        DirectoryDbState.Loading -> {
                            _listSongsFromDeviceNew.value = SongDbState.Loading
                            _autoPlayListSongsNew.value = SongDbState.Loading
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

                            if (savedSongsState is SongDbState.Success)
                            savedSongsState.songs.forEach {
                                Log.d("searching bug", "list song = ${Uri.fromFile(File(it.uri)).path}")
                            }

                            _listSongsFromDeviceNew.value = SongDbState.Success(correctSongsList)
                            _autoPlayListSongsNew.value = SongDbState.Success(songsFromDevice.filter { !it.disEnableAutoPlay })
                            _songsNotSavedYetNew.value = if (savedSongsState is SongDbState.Success) {
                                SongDbState.Success(correctSongsList.filterNot { song -> savedSongsState.songs.contains(song) })
                            } else {
                                SongDbState.Success(songsFromDevice)
                            }

                            Log.d("searching bug", "list song = ${_listSongsFromDeviceNew.value}")
                            Log.d("searching bug", "auto play song = ${_autoPlayListSongsNew.value}")
                            Log.d("searching bug", "song not saved = ${_songsNotSavedYetNew.value}")
                            Log.d("searching bug", "song saved = ${_listSavedSongsNew.value}")
                            Log.d("searching bug", "song from db = ${songFromDb}")
                        }
                    }
                }.collect()
            }

//            launch {
//                dbSongRepository.getAllSongs().collect { list ->
//                    val mappedListOfMusic = list.mapNotNull {
//                        it
//                    }
//                    if (mappedListOfMusic.isNotEmpty()){
//                        _listSavedSongsNew.value = SongDbState.Success(mappedListOfMusic)
//                    } else{
//                        _listSavedSongsNew.value = SongDbState.Empty
//                    }
//                }
//            }
//
//            launch {
//                directoryRepository.getActiveDirectoriesState().collect{directoryState ->
//                    when(directoryState){
//                        is DirectoryDbState.Loading -> {
//                            _listSongsFromDeviceNew.value = SongDbState.Loading
//                            _autoPlayListSongsNew.value = SongDbState.Loading
//                            _songsNotSavedYetNew.value = SongDbState.Loading
//                        }
//                        is DirectoryDbState.Empty -> {
//                            _listSongsFromDeviceNew.value = SongDbState.Empty
//                            _autoPlayListSongsNew.value = SongDbState.Empty
//                            _songsNotSavedYetNew.value = SongDbState.Empty
//                        }
//                        is DirectoryDbState.Success -> {
//                            Log.d("searching bug", " state = $directoryState")
//                            when(val savedSongsState = _listSavedSongsNew.value){
//                                is SongDbState.Success -> {
//                                    val songsFromDevice = songSource.getAudioFileFromDirectories(
//                                        directories = directoryState.directories
//                                    )
//                                    val correctSongsList = mutableListOf<Song>()
//                                    songsFromDevice.forEach { songFromDevice ->
//                                        correctSongsList.add(
//                                            if (savedSongsState.songs.map { savedSong -> savedSong.uri }.contains(songFromDevice.uri))
//                                                savedSongsState.songs[savedSongsState.songs.map {savedSong -> savedSong.uri}.indexOf(songFromDevice.uri)]
//                                            else songFromDevice
//                                        )
//                                    }
//                                    _listSongsFromDeviceNew.value = SongDbState.Success(correctSongsList)
//                                    _autoPlayListSongsNew.value = SongDbState.Success(songsFromDevice.filter { song -> !song.disEnableAutoPlay })
//                                    _songsNotSavedYetNew.value =
//                                        SongDbState.Success(correctSongsList.filterNot { song -> savedSongsState.songs.contains(song) })
//
//                                    Log.d("searching bug", "list song = ${_listSongsFromDeviceNew.value}")
//                                    Log.d("searching bug", " auto play song = ${_autoPlayListSongsNew.value}")
//                                    Log.d("searching bug", " song not saved = ${_songsNotSavedYetNew.value}")
//                                }
//                                is SongDbState.Empty -> {
//                                    val songsFromDevice = songSource.getAudioFileFromDirectories(
//                                        directories = directoryState.directories
//                                    )
//
//                                    _listSongsFromDeviceNew.value = SongDbState.Success(songsFromDevice)
//                                    _autoPlayListSongsNew.value = SongDbState.Success(songsFromDevice.filter { song -> !song.disEnableAutoPlay })
//                                    _songsNotSavedYetNew.value = SongDbState.Success(songsFromDevice)
//
//                                    Log.d("searching bug", "list song = ${_listSongsFromDeviceNew.value}")
//                                    Log.d("searching bug", " auto play song = ${_autoPlayListSongsNew.value}")
//                                    Log.d("searching bug", " song not saved = ${_songsNotSavedYetNew.value}")
//                                }
//
//                                is SongDbState.Loading -> {
//                                    val songsFromDevice = songSource.getAudioFileFromDirectories(
//                                        directories = directoryState.directories
//                                    )
//
//                                    _listSongsFromDeviceNew.value = SongDbState.Success(songsFromDevice)
//                                    _autoPlayListSongsNew.value = SongDbState.Loading
//                                    _songsNotSavedYetNew.value = SongDbState.Loading
//
//                                    Log.d("searching bug", "list song = ${_listSongsFromDeviceNew.value}")
//                                    Log.d("searching bug", " auto play song = ${_autoPlayListSongsNew.value}")
//                                    Log.d("searching bug", " song not saved = ${_songsNotSavedYetNew.value}")
//                                }
//                            }
//                        }
//                    }
//                }
//            }

//            combine(
//                dbSongRepository.getAllSongs(),
//                directoryRepository.getActiveDirectoriesState()
//            ) { songList, directoryState ->
//                val mappedListOfMusic = songList.mapNotNull { it }
//                val savedSongsState = if (mappedListOfMusic.isNotEmpty()) {
//                    SongDbState.Success(mappedListOfMusic)
//                } else {
//                    SongDbState.Empty
//                }
//
//                when (directoryState) {
//                    is DirectoryDbState.Loading -> {
//                        _listSongsFromDeviceNew.value = SongDbState.Loading
//                        _autoPlayListSongsNew.value = SongDbState.Loading
//                        _songsNotSavedYetNew.value = SongDbState.Loading
//                    }
//
//                    is DirectoryDbState.Empty -> {
//                        _listSongsFromDeviceNew.value = SongDbState.Empty
//                        _autoPlayListSongsNew.value = SongDbState.Empty
//                        _songsNotSavedYetNew.value = SongDbState.Empty
//                    }
//
//                    is DirectoryDbState.Success -> {
//                        Log.d("searching bug", " state = $directoryState")
//                        val songsFromDevice = songSource.getAudioFileFromDirectories(
//                            directories = directoryState.directories
//                        )
//
//                        val correctSongsList = if (savedSongsState is SongDbState.Success) {
//                            songsFromDevice.map { songFromDevice ->
//                                savedSongsState.songs.find { it.uri == songFromDevice.uri } ?: songFromDevice
//                            }
//                        } else {
//                            songsFromDevice
//                        }
//
//                        _listSongsFromDeviceNew.value = SongDbState.Success(correctSongsList)
//                        _autoPlayListSongsNew.value = SongDbState.Success(songsFromDevice.filter { !it.disEnableAutoPlay })
//                        _songsNotSavedYetNew.value = if (savedSongsState is SongDbState.Success) {
//                            SongDbState.Success(correctSongsList.filterNot { song -> savedSongsState.songs.contains(song) })
//                        } else {
//                            SongDbState.Success(songsFromDevice)
//                        }
//
//                        Log.d("searching bug", "list song = ${_listSongsFromDeviceNew.value}")
//                        Log.d("searching bug", "auto play song = ${_autoPlayListSongsNew.value}")
//                        Log.d("searching bug", "song not saved = ${_songsNotSavedYetNew.value}")
//                    }
//                }
//            }.collect()



            launch {
                _listSavedSongs.collect{newSavedSongs->
                    val listSongs = _listSongsFromDevice.value ?: listOf()
                    val oldSavedSongs = _listSavedSongs.value
                    val equalLists = newSavedSongs?.map { Uri.parse(it.uri) } == oldSavedSongs?.map { Uri.parse(it.uri) }
                    if (equalLists) return@collect
                    _songsNotSavedYet.value = listSongs.filterNot { song ->
                            newSavedSongs?.contains(song) ?: true
                    }
                }
            }

            //            launch {
//                directoryRepository.getListOfActiveDirectory().collect { listFromDb ->
//                    if (listFromDb == null) return@collect
//                    Log.d("debug 22333"," inMusicRepository for all dir $listFromDb")
//
//                    val listActiveDirectories = listFromDb
//                    val songsFromDb = _listSavedSongs.value ?: dbSongRepository.getAllSongs().first().mapNotNull { it }
//                    Log.d("debug 22333"," inMusicRepository for saved song $songsFromDb")
//
//                    val songsFromDevice = songSource.getAudioFileFromDirectories(
//                        directories = listActiveDirectories,
//                        songsFromDb = songsFromDb
//                    )
//                    _listSongsFromDevice.value = songsFromDevice
//
//                    Log.d("debug 22333"," inMusicRepository for all song${songsFromDevice.filterNot { song -> songsFromDb.contains(song) }}")
//                    _songsNotSavedYet.value = songsFromDevice.filterNot { song -> songsFromDb.contains(song) }
//
//                    val autoPlayList = songsFromDb.filter { song -> !song.disEnableAutoPlay }
//                    _autoPlayListSongs.value = autoPlayList
//                }
//            }

        }
    }


//    fun getListSavedSongs(): Flow<List<Song>?> = _listSavedSongs
//
//    fun getListSongsFromDevice(): Flow<List<Song>?> = _listSongsFromDevice
//
//    fun getListSongsNotSavedYet(): Flow<List<Song>?> = _songsNotSavedYet

    fun getListSavedSongs(): Flow<SongDbState> = _listSavedSongsNew

    fun getListSongsFromDevice(): Flow<SongDbState> = _listSongsFromDeviceNew

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
        return _listSongsFromDevice.value?.firstOrNull {uri == it.uri}
    }

    fun refreshSongsFromDevice() {
        customScope.launch {
            //todo
            val listActiveDirectories = directoryRepository.getListOfActiveDirectory().first() ?: return@launch
            val songsFromDb = _listSavedSongs.value.orEmpty()

            val songsFromDevice = songSource.getAudioFileFromDirectories(
                directories = listActiveDirectories,
                songsFromDb = songsFromDb
            )

            _listSongsFromDevice.value = songsFromDevice
        }
    }

    sealed class SongDbState{
        data object Loading : SongDbState()
        data class Success(val songs: List<Song>): SongDbState()
        data object Empty: SongDbState()
    }

}