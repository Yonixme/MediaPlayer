package com.example.fullproject.screens.musiclist.oldr

import android.util.Log
import com.example.fullproject.App
import com.example.fullproject.model.songpack.entities.Song
import com.example.fullproject.screens.viewmodels.oldr.BaseMusicViewModelOLD
import kotlinx.coroutines.runBlocking

class MusicListViewModelOLD(private val app: App) : BaseMusicViewModelOLD(app) {

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