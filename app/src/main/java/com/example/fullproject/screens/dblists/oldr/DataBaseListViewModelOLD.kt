package com.example.fullproject.screens.dblists.oldr


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.App
import com.example.fullproject.DBRepositories
import com.example.fullproject.model.dirpack.entities.Directory
import com.example.fullproject.model.songpack.entities.MetaDataSong
import com.example.fullproject.screens.viewmodels.oldr.BaseListViewModelOLD

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataBaseListViewModelOLD(private val app: App) : ViewModel(){

    fun updateFlagAutoPlaySong(id: Long, isChecked: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            BaseListViewModelOLD.Base().activateFlagAutoPlaySong(id, isChecked) }
    }

    fun updateFlagAddPlaylistDir(id: Long, isChecked: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            BaseListViewModelOLD.Base().activateFlagAddPlaylistDir(id, isChecked) }
    }

    fun findIdByUri(uri: String): Long{
        var id = -1 as Long
        viewModelScope.launch(Dispatchers.IO) {
            id = BaseListViewModelOLD.Base().findIdByUri(uri) }
        return id
    }

    fun getListSong(onlyActive: Boolean): List<MetaDataSong> = runBlocking(Dispatchers.IO){
        return@runBlocking BaseListViewModelOLD.Base().getListSongWithDB(onlyActive)
    }

    fun getListDir(onlyActive: Boolean): List<Directory> = runBlocking(Dispatchers.IO){
        return@runBlocking BaseListViewModelOLD.Base().getListDirWithDB(onlyActive)
    }

    fun deleteSongElement(id: Long) = runBlocking{
        DBRepositories.metaSongsRepository.deleteSongObject(id)
    }

    fun deleteDirElement(id: Long) = runBlocking{
        DBRepositories.dirRepository.deleteDirObject(id)
    }

    fun writeDirInDB(uri: String,
                     name: String?,
                     addToStackPlaying: Boolean) = runBlocking {
                         DBRepositories.dirRepository.createDirObject(uri, name, addToStackPlaying, false)
    }

    fun writeSongInDB(uri: String,
                      name: String?,
                      author: String?,
                      addToStackPlaying: Boolean
    ) = runBlocking {
        DBRepositories.metaSongsRepository.createSongObject(uri, name, author, null, addToStackPlaying)
    }
}