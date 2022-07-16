package com.example.fullproject.view

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.fullproject.App
import com.example.fullproject.businesslogic.SongMusic

class MusicListViewModel(private val app: App) : ViewModel() {

    fun onSoundPlay(context: Context, songMusic: SongMusic) {
        app.soundServiceMusic.playSound(context,songMusic)
    }

    fun onSoundPause(songMusic: SongMusic) {
        app.soundServiceMusic.soundPause(songMusic)
    }

    fun onSoundStop(songMusic: SongMusic) {
        app.soundServiceMusic.soundStop(songMusic)
    }
}