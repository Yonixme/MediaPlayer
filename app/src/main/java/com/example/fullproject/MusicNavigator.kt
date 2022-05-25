package com.example.fullproject

import android.content.Context
import com.example.fullproject.model.Song

interface MusicNavigator {
    fun onSoundPlay(id: Int)

    fun onSoundPause()

    fun onSoundStop()
}