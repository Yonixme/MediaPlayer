package com.example.fullproject.screens.musiclist

import android.util.Log
import com.example.fullproject.App
import com.example.fullproject.screens.BaseListViewModel
import com.example.fullproject.screens.BaseMusicViewModel
import com.example.fullproject.services.model.songpack.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MusicListViewModel(private val app: App) : BaseMusicViewModel(app) {

    fun getSongsList(): List<Song> = runBlocking{
        Log.d("DataBaseURI", app.getMusicService().songs.size.toString())
        return@runBlocking app.getMusicService().songs
    }

    fun getLastSong(): Song{
        Log.d("DataBaseURI", app.getMusicService().songs.size.toString())
        return app.getMusicService().lastSong
    }

    fun notifyUserWhatElementInCreating(){
        notifyUser("This element is creating")
    }
}