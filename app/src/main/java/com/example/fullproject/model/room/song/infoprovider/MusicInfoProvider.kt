package com.example.fullproject.model.room.song.infoprovider

import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails

interface MusicInfoProvider {


    fun getInformationForSong(song: SongNew): SongWithDetails?

    fun getCurrentMediaPlayer(): MediaPlayer?

    fun changeCurrentMediaPlayer(uri: String)

    fun createMediaPlayer(uri: String): MediaPlayer?
}