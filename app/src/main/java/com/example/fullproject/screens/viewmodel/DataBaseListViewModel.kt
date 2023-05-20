package com.example.fullproject.screens.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.App
import com.example.fullproject.Repositories
import com.example.fullproject.model.sqlite.dirpack.entities.Dir
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataBaseListViewModel(private val app: App) : ViewModel(){

    fun updateFlagAutoPlaySong(id: Long, isChecked: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            BaseListViewModel.Base().activateFlagAutoPlaySong(id, isChecked) }
    }

    fun updateFlagAddPlaylistDir(id: Long, isChecked: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            BaseListViewModel.Base().activateFlagAddPlaylistDir(id, isChecked) }
    }

    fun findIdByUri(uri: String): Long{
        var id = -1 as Long
        viewModelScope.launch(Dispatchers.IO) {
            id = BaseListViewModel.Base().findIdByUri(uri) }
        return id
    }

    fun getListSong(onlyActive: Boolean): List<MetaDataSong> = runBlocking(Dispatchers.IO){
        return@runBlocking BaseListViewModel.Base().getListSongWithDB(onlyActive)
    }

    fun getListDir(onlyActive: Boolean): List<Dir> = runBlocking(Dispatchers.IO){
        return@runBlocking BaseListViewModel.Base().getListDirWithDB(onlyActive)
    }

    fun deleteSongElement(id: Long) = runBlocking{
        Repositories.metaSongsRepository.deleteSongObject(id)
    }

    fun deleteDirElement(id: Long) = runBlocking{
        Repositories.dirRepository.deleteDirObject(id)
    }

    fun writeDirInDB(uri: String,
                     name: String?,
                     addToStackPlaying: Boolean) = runBlocking {
                         Repositories.dirRepository.createDirObject(uri, name, addToStackPlaying, false)
    }

    fun writeSongInDB(uri: String,
                      name: String?,
                      author: String?,
                      addToStackPlaying: Boolean
    ) = runBlocking {
        Repositories.metaSongsRepository.createSongObject(uri, name, author, null, addToStackPlaying)
    }
}