package com.example.fullproject.model.room.song.controller

import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails

interface MusicController {
    fun getInformationForSong(song: SongNew): SongWithDetails?

    fun getIsPlayingMusicState(): Boolean

    fun getCurrentPosition(): Int

    fun getDuration(): Int

    fun playMusic(uri: String)

    fun pauseMusic()

    fun stopMusic()

    fun setCurrentTimeInMillis(newTime: Int)

    fun setActionOnFinish(block: () -> Unit)

    fun pausePlaying()

    fun continuePlaying()

    fun changeSong(uri: String)
}