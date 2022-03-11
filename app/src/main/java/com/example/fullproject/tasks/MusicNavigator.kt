package com.example.fullproject.tasks

import android.content.Context
import com.example.fullproject.model.Song

interface MusicNavigator {
    fun playSoundPlayer(id: Int)

    fun pauseSoundPlayer()

    fun stopSoundPlayer()

    fun add(song: Song)

    fun delete(song: Song)
}