package com.example.fullproject.model.room.song

import com.example.fullproject.model.room.directory.controller.DirectoryRepository
import com.example.fullproject.model.room.song.database.DbSongRepository
import com.example.fullproject.model.room.song.entities.SongData
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails
import com.example.fullproject.model.room.song.infoprovider.MusicInfoProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val dbSongRepository: DbSongRepository,
    private val directoryRepository: DirectoryRepository,
    private val songSource: SongSource
){
    //rewrite in future
    val currentSong: SongWithDetails? = null
        get()  {
            return field?.copy()
        }

    private val customScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _listSavedSongs: MutableStateFlow<List<SongNew>?> = MutableStateFlow(null)
    private val _listSongsFromDevice: MutableStateFlow<List<SongNew>?> = MutableStateFlow(null)
    private val _autoPlayListSongs: MutableStateFlow<List<SongNew>?> = MutableStateFlow(null)

    init {
        customScope.launch {
            launch {
                dbSongRepository.getAllSongs().collect { list ->
                    val mappedListOfMusic = list.mapNotNull {
                        it
                    }
                    _listSavedSongs.value = mappedListOfMusic
                }
            }

            launch {
                directoryRepository.getListOfActiveDirectory().collect { listFromDb ->
                    val listActiveDirectories = listFromDb.orEmpty()
                    val songsFromDb = _listSavedSongs.value.orEmpty()

                    val songsFromDevice = songSource.getAudioFileFromDirectories(
                        directories = listActiveDirectories,
                        songsFromDb = songsFromDb
                    )
                    println("DebugDb123 ${songsFromDevice}")

                    _listSongsFromDevice.value = songsFromDevice



                    val autoPlayList = songsFromDb.filter { song -> !song.disEnableAutoPlay }
                    _autoPlayListSongs.value = autoPlayList
                }
            }
        }
    }


    fun getListSavedSongs(): Flow<List<SongNew>?> = _listSavedSongs

    fun getListSongsFromDevice(): Flow<List<SongNew>?> = _listSongsFromDevice

    suspend fun addSongInDb(songData: SongData){
        dbSongRepository.addSong(songData)
    }

    suspend fun deleteSongInDb(id: Long){
        dbSongRepository.deleteSong(id)
    }

    suspend fun updateSongInDb(songNew: SongNew){
        dbSongRepository.updateValueForSong(songNew)
    }

    fun getSongByURI(uri: String):SongNew?{
        return _listSongsFromDevice.value?.firstOrNull {uri == it.uri}
    }

}