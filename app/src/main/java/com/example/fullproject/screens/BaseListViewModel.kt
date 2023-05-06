package com.example.fullproject.screens

import com.example.fullproject.Repositories
import com.example.fullproject.services.model.dirpack.entities.Dir
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import kotlinx.coroutines.flow.collect

interface BaseListViewModel {
    suspend fun getListSongWithDB(onlyActive: Boolean): List<MetaDataSong>

    suspend fun activateFlagAutoPlaySong(id: Long, newValue: Boolean)

    suspend fun activateFlagAddPlaylistDir(id: Long, newValue: Boolean)

    suspend fun findIdByUri(uri: String): Long

    suspend fun getListDirWithDB(onlyActive: Boolean): List<Dir>

    class Base(): BaseListViewModel{
        override suspend fun getListSongWithDB(onlyActive: Boolean): List<MetaDataSong> {
            val list: MutableList<MetaDataSong> = mutableListOf()

            Repositories.songsRepository.getSongs(false)
                .collect{
                    for (l in it)
                        list.add(l)
                }

            return list.toList()
        }

        override suspend fun activateFlagAutoPlaySong(id: Long, newValue: Boolean) {
            Repositories.songsRepository.updateFlagItem(id, newValue)
        }

        override suspend fun findIdByUri(uri: String): Long {
            return Repositories.songsRepository.findSongIdByURI(uri)
        }

        override suspend fun getListDirWithDB(onlyActive: Boolean): List<Dir> {
            var list = listOf<Dir>()
            Repositories.dirRepository.getDirList(onlyActive)
                .collect{
                    list = it
                }
            return list
        }

        override suspend fun activateFlagAddPlaylistDir(id: Long, newValue: Boolean) {
            Repositories.dirRepository.updateFlagItem(id, newValue)
        }
    }

}