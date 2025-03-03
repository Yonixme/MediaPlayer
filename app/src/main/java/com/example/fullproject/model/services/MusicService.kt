package com.example.fullproject.model.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.example.fullproject.model.services.notification.NotificationHelper
import com.example.fullproject.model.song.MusicRepository
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.model.song.provider.controller.MusicController
import com.example.fullproject.model.song.provider.infoprovider.MusicInfoProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service(){
    @Inject lateinit var musicController: MusicController
    @Inject lateinit var musicRepository: MusicRepository
    @Inject lateinit var musicInfoProvider: MusicInfoProvider
    @Inject lateinit var notificationHelper: NotificationHelper

    private var timerJob: Job? = null
    private var foregroundIsActive = false
    private val customScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _currentSongFlow: MutableStateFlow<SongWithDetails?> = MutableStateFlow(null)
    private var lastSong: SongWithDetails? = null

    private val _autoPlayListSongs: MutableStateFlow<List<Song>?> = MutableStateFlow(null)
    private val _listSongs: MutableStateFlow<List<Song>?> = MutableStateFlow(null)

    override fun onCreate() {
        super.onCreate()
        customScope.launch {
            launch {
                musicRepository.getListSongsFromDevice().collect {state ->
                    when(state){
                        MusicRepository.SongDbState.Empty -> _listSongs.value = emptyList()
                        MusicRepository.SongDbState.Loading -> _listSongs.value = null
                        is MusicRepository.SongDbState.Success -> {
                            _listSongs.value = state.songs
                            val filteredList = state.songs.filter { song -> !song.disEnableAutoPlay }
                            _autoPlayListSongs.value = filteredList
                        }
                    }
                }
            }

            launch {
                _currentSongFlow.collect{
                    if (foregroundIsActive) {
                        notificationHelper.updateNotification(it)
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val uri = intent.getStringExtra(ID_URI) ?: _currentSongFlow.value?.song?.uri ?: lastSong?.song?.uri ?: return START_NOT_STICKY
        if (_currentSongFlow.value?.song?.uri != uri) {
            onStopMusic()
            val newSong = musicRepository.getSongByURI(uri) ?: return START_NOT_STICKY
            _currentSongFlow.value = musicInfoProvider.getInformationForSong(newSong)
        }
        if (!foregroundIsActive) {
            val currentSong = _currentSongFlow.value
            startForeground(1, notificationHelper.createNotification(currentSong))
            foregroundIsActive = true
        }


        when (intent.action) {
            COMMAND_ON_PLAY_MUSIC -> onPlayMusic()
            COMMAND_ON_PAUSE_MUSIC -> onPauseMusic()
            COMMAND_CONTINUE_PLAYING -> continuePlaying()
            COMMAND_PAUSE_PLAYING -> pausePlaying()
            COMMAND_NEXT_SONG -> nextSong()
            COMMAND_PREVIOUS_SONG -> previousSong()
            COMMAND_ON_STOP_MUSIC -> {
                onStopMusic()
                if (foregroundIsActive){
                    if (Build.VERSION.SDK_INT < 24)
                        stopForeground(true)
                    else
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    foregroundIsActive = false
                }
                stopSelf()
            }
            else -> {
                onStopMusic()
                if (foregroundIsActive){
                    if (Build.VERSION.SDK_INT < 24)
                        stopForeground(true)
                    else
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    foregroundIsActive = false
                }
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        customScope.coroutineContext.cancel()
    }

    private fun onPlayMusic(){
        val playingSongWithDetails = _currentSongFlow.value ?: return
        if (playingSongWithDetails.isPlaying) return
        musicController.playMusic(playingSongWithDetails.song.uri)
        musicController.setActionOnFinish {
            nextSong()
            updateSongState()
        }
        if (timerJob == null) startUpdatingTimer()
        val newSong = _currentSongFlow.value
        _currentSongFlow.value = newSong?.copy(
            isPlaying = musicController.getIsPlayingMusicState(),
            currentPosition = musicController.getCurrentPosition())
    }

    private fun onPauseMusic(){
        val playingSongWithDetails = _currentSongFlow.value ?: return
        if(!playingSongWithDetails.isPlaying) return
        musicController.pauseMusic()
        if (timerJob != null) stopTimer()
        val newSong = _currentSongFlow.value
        _currentSongFlow.value = newSong?.copy(
            isPlaying = musicController.getIsPlayingMusicState(),
            currentPosition = musicController.getCurrentPosition())

    }

    private fun onStopMusic(){
        lastSong = _currentSongFlow.value
        if (_currentSongFlow.value == null) return
        _currentSongFlow.value = null
        musicController.stopMusic()
        if (timerJob != null) stopTimer()
        val newSong = _currentSongFlow.value
        _currentSongFlow.value = newSong?.copy(
            isPlaying = musicController.getIsPlayingMusicState(),
            currentPosition = musicController.getCurrentPosition())
    }

    private fun pausePlaying(){
        musicController.pausePlaying()
        if (timerJob != null) stopTimer()
    }

    private fun continuePlaying(){
        musicController.continuePlaying()
        if (timerJob == null) startUpdatingTimer()
    }

    private val binder = GetServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class GetServiceBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    fun getCurrentSongFlow(): Flow<SongWithDetails?> = _currentSongFlow

    fun setCurrentTime(currentTime: Int){
        musicController.setCurrentTimeInMillis(currentTime){
            nextSong()
            updateSongState()
        }
    }

    fun updateSongState(){
        _currentSongFlow.value = _currentSongFlow.value?.copy(
            duration = musicController.getDuration(),
            currentPosition = musicController.getCurrentPosition(),
            isPlaying = musicController.getIsPlayingMusicState()
        )
    }


    private fun nextSong(fromAutoPlayList: Boolean = false) = changeCurrentSong(1, fromAutoPlayList)

    private fun previousSong(fromAutoPlayList: Boolean = false) = changeCurrentSong(-1, fromAutoPlayList)

    private fun changeCurrentSong(direction: Int, fromAutoPlayList: Boolean) {

        val currentSong = _currentSongFlow.value?.song ?: return
        val musicList = _listSongs.value ?: return
        val autoPlayList = _autoPlayListSongs.value ?: emptyList()

        val currentList = if (fromAutoPlayList) autoPlayList else musicList

        val currentIndex = currentList.indexOfFirst { it.uri == currentSong.uri }
        if (currentIndex == -1) return

        if ((direction == 1 && currentIndex >= currentList.lastIndex) ||
            (direction == -1 && currentIndex <= 0)) return

        val newIndex = currentIndex + direction

        val newSong = if (fromAutoPlayList) {
            findNextInAutoPlayList(musicList, autoPlayList, musicList[newIndex], direction) ?: return
        } else {
            musicList.getOrNull(newIndex) ?: return
        }
        musicController.changeSong(newSong.uri)
        if (timerJob == null) startUpdatingTimer()
        _currentSongFlow.value = musicController.getInformationForSong(newSong)
    }

    private fun findNextInAutoPlayList(
        listSongs: List<Song>,
        autoPlayList: List<Song>,
        target: Song,
        direction: Int
    ): Song? {
        val indexInList = listSongs.indexOfFirst { it.uri == target.uri }

        val nextAutoPlaySong = if (direction == 1) {
            listSongs.drop(indexInList + 1).firstOrNull { it in autoPlayList }
        } else {
            listSongs.take(indexInList).lastOrNull { it in autoPlayList }
        }

        return nextAutoPlaySong
    }

    private fun startUpdatingTimer(){
        if (timerJob == null) {
            timerJob = CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    updateSongState()
                    delay(1000L)
                }
            }
        }
    }

    private fun stopTimer(){
        if (timerJob != null) {
            timerJob?.cancel()
            timerJob = null
        }
    }

    companion object{
        const val COMMAND_ON_PLAY_MUSIC = "on play music"
        const val COMMAND_ON_PAUSE_MUSIC = "on pause music"
        const val COMMAND_ON_STOP_MUSIC = "on stop music"
        const val COMMAND_PAUSE_PLAYING = "pause playing"
        const val COMMAND_CONTINUE_PLAYING = "continue playing"
        const val COMMAND_NEXT_SONG = "next song"
        const val COMMAND_PREVIOUS_SONG = "previous song"
        const val ID_URI = "uri"
    }
}