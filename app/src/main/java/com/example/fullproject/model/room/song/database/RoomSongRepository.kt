package com.example.fullproject.model.room.song.database

import com.example.fullproject.model.room.song.database.entities.SongDbEntity
import com.example.fullproject.model.room.song.entities.SongData
import com.example.fullproject.model.room.song.entities.SongNew
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomSongRepository @Inject constructor(
    private val songDao: SongDao
) : DbSongRepository {

    override fun getAllSongs(): Flow<List<SongNew?>> {
        return songDao.getAllSongs()
            .map { list ->
                list.map {
                    it?.toSong()
                }
            }
    }

    override suspend fun deleteSong(id: Long) {
        songDao.deleteSong(id)
    }

    override suspend fun  addSong(songData: SongData) {
        songDao.insertSong(SongDbEntity.fromSongData(songData))
    }

    override suspend fun updateValueForSong(songNew: SongNew) {
        songDao.updateValueForSong(SongDbEntity.fromSong(songNew))
    }

    override suspend fun findByURI(uri: String): SongNew? {
        return songDao.findByURI(uri)?.toSong()
    }
}