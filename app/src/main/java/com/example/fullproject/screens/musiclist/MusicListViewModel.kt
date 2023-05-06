package com.example.fullproject.screens.musiclist

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.fullproject.App
import com.example.fullproject.Repositories
import com.example.fullproject.services.model.SongMusic
import com.example.fullproject.screens.BaseMusicViewModel
import com.example.fullproject.services.model.songpack.SQLiteSongRepository
import kotlinx.coroutines.launch

class MusicListViewModel(private val app: App) : BaseMusicViewModel(app) {
    fun onMusicPlayer(song: SongMusic){
        Log.d("onMusicPlayer", "Stop")
        app.soundServiceMusic.pauseTimeSound(song)

        viewModelScope.launch {
            Repositories.init(app.applicationContext)

            Repositories.songsRepository.getSongs(false)
                .collect {
                  //  for (s in it) {
                   //     Log.d("DataBaseURI", s.uri)
                    //}
                }
        }
    }

    init {
        viewModelScope.launch {
            Repositories.songsRepository.getSongs(false)
                .collect {
                   // for (s in it) {
                       // Log.d("DataBaseURI", s.name?:"Null")
                    //}
                }
        }
    }
}