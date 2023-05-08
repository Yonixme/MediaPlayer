package com.example.fullproject.screens.dblists


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.App
import com.example.fullproject.screens.BaseListViewModel
import com.example.fullproject.services.model.dirpack.entities.Dir
import com.example.fullproject.services.model.songpack.entities.MetaDataSong

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

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
}