package com.example.fullproject.screens.viewmodels.oldr

import com.example.fullproject.DBRepositories
import com.example.fullproject.model.dirpack.entities.Directory
import com.example.fullproject.model.songpack.entities.MetaDataSong

interface BaseListViewModelOLD {
    suspend fun getListSongWithDB(onlyActive: Boolean): List<MetaDataSong>

    suspend fun activateFlagAutoPlaySong(id: Long, newValue: Boolean)

    suspend fun activateFlagAddPlaylistDir(id: Long, newValue: Boolean)

    suspend fun findIdByUri(uri: String): Long

    suspend fun getListDirWithDB(onlyActive: Boolean): List<Directory>

    class Base(): BaseListViewModelOLD {

        override suspend fun getListSongWithDB(onlyActive: Boolean): List<MetaDataSong> {
            val list: MutableList<MetaDataSong> = mutableListOf()

            DBRepositories.metaSongsRepository.getSongs(false)
                .collect{
                    for (l in it)
                        list.add(l)
                }

            return list.toList()
        }

        override suspend fun activateFlagAutoPlaySong(id: Long, newValue: Boolean) {
            DBRepositories.metaSongsRepository.setAutoPlayFlag(id, newValue)
        }

        override suspend fun findIdByUri(uri: String): Long {
            return DBRepositories.metaSongsRepository.findSongIdByURI(uri)
        }

        override suspend fun getListDirWithDB(onlyActive: Boolean): List<Directory> {
            var list = listOf<Directory>()
            DBRepositories.dirRepository.getDirList(onlyActive)
                .collect(){
                    list = it
                }
            return list
        }

        override suspend fun activateFlagAddPlaylistDir(id: Long, newValue: Boolean) {
            DBRepositories.dirRepository.setReadFlag(id, newValue)
        }
    }

}