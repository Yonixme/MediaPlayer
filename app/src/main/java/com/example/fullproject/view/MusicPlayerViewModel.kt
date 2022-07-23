package com.example.fullproject.view

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.fullproject.App
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.businesslogic.SoundServiceMusic

class MusicPlayerViewModel(private val app: App) : ViewModel() {
    private var soundService: SoundServiceMusic = getSoundService()
    var currentTime: Long = 0
    var song: SongMusic = SongMusic(Uri.parse("Empty"))


    private fun getSoundService():SoundServiceMusic = app.soundServiceMusic

}