package com.example.fullproject.view

import android.util.Log
import com.example.fullproject.App
import com.example.fullproject.businesslogic.SongMusic

class MusicListViewModel(private val app: App) : BaseMusicViewModel(app) {
    fun onMusicPlayer(song: SongMusic){
        Log.d("onMusicPlayer", "Stop")
        app.soundServiceMusic.pauseTimeSound(song)
    }
}