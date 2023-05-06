package com.example.fullproject.screens.dblists


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.App
import com.example.fullproject.screens.BaseListViewModel
import com.example.fullproject.services.model.dirpack.entities.Dir
import com.example.fullproject.services.model.songpack.entities.MetaDataSong

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

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

    suspend fun getListSong(onlyActive: Boolean): List<MetaDataSong>{
        return BaseListViewModel.Base().getListSongWithDB(onlyActive)
    }

    suspend fun getListDir(onlyActive: Boolean): List<Dir>{
        return BaseListViewModel.Base().getListDirWithDB(onlyActive)
    }
}