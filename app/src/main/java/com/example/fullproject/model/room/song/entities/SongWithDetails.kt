package com.example.fullproject.model.room.song.entities

import android.media.MediaPlayer

data class SongWithDetails(
    val song: SongNew,
    val duration: Int,
    val isPlaying: Boolean,
    val currentPosition: Int
)

data class SongWithDetails2(
    val song: SongNew,
    val mediaPlayer: MediaPlayer?
){
    fun getDuration(): Int?{
        return mediaPlayer?.duration
    }

    fun getCurrentPosition(): Int?{
        return mediaPlayer?.currentPosition
    }

    fun getIsPlatingStatus(): Boolean?{
        return mediaPlayer?.isPlaying
    }
}