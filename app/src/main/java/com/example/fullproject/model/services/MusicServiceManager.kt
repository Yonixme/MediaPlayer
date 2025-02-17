package com.example.fullproject.model.services

import com.example.fullproject.model.song.entities.SongWithDetails
import kotlinx.coroutines.flow.Flow

interface MusicServiceManager {
    //fun getSongListWithDetails(): Flow<List<SongWithDetails>?>

    fun getCurrentSongWithDetails(): Flow<CurrentSongState?>

    fun onPlay(uri: String)

    fun onPause(uri: String)

    fun onStop(uri: String)

    fun setCurrentTime(currentTime: Int)

    fun getIsPlaying(): Boolean

    fun bindService()

    fun unBindService()

    fun updateSongState()

    fun pauseMusic(uri: String)

    fun continueMusic(uri: String)

    fun nextSong(uri: String)

    fun previousSong(uri: String)

    sealed class CurrentSongState{
        data object Loading : CurrentSongState()
        data class Success(val currentSong: SongWithDetails?): CurrentSongState()
        data class Error(val massage: String) : CurrentSongState()
        data object Empty: CurrentSongState()
    }
}