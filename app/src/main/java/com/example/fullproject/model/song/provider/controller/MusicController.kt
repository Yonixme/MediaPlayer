package com.example.fullproject.model.song.provider.controller

import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongWithDetails

interface MusicController {
    fun getInformationForSong(song: Song): SongWithDetails?

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