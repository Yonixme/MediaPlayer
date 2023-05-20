package com.example.fullproject.screens.viewmodel

import android.util.Log
import com.example.fullproject.App
import com.example.fullproject.model.Song
import kotlinx.coroutines.runBlocking

class MusicListViewModel(private val app: App) : BaseMusicViewModel(app) {

    fun getSongsList(): List<Song> = runBlocking{
        Log.d("DataBaseURI", app.getMusicService().songs.size.toString())
        return@runBlocking app.getMusicService().songs
    }

    fun getLastSong(): Song {
        Log.d("DataBaseURI", app.getMusicService().songs.size.toString())
        return app.getMusicService().lastSong
    }

    fun notifyUserWhatElementWasTouched(){
        notifyUser("Name was updated")
    }
}