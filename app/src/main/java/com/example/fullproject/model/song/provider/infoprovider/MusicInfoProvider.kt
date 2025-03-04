package com.example.fullproject.model.song.provider.infoprovider

import android.media.MediaPlayer
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongWithDetails

interface MusicInfoProvider {


    fun getInformationForSong(song: Song): SongWithDetails?
}