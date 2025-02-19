package com.example.fullproject.model.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fullproject.MusicBroadcast
import com.example.fullproject.R
import com.example.fullproject.model.services.notification.NotificationHelper
import com.example.fullproject.model.song.MusicRepository
import com.example.fullproject.model.song.provider.controller.MusicController
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.model.song.provider.infoprovider.MusicInfoProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service(){
    @Inject lateinit var musicController: MusicController
    @Inject lateinit var musicRepository: MusicRepository
    @Inject lateinit var musicInfoProvider: MusicInfoProvider
    @Inject lateinit var notificationHelper: NotificationHelper

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
                musicRepository.getListSongsFromDevice()
                    .collect{listSongFromDevice ->
                        _listSongs.value = listSongFromDevice
                        val filteredList = listSongFromDevice?.filter { song -> !song.disEnableAutoPlay }
                        _autoPlayListSongs.value = filteredList
                    }
            }
        }
//        foregroundIsActive = true
//        startForeground(1, notificationHelper.createNotification())


//        createNotificationChannel()
//        val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
//            .setContentTitle("Music Player")
//            .setSmallIcon(R.drawable.music_image)
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .build()
//        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("massage123 in service")
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

        customScope.launch {
            _currentSongFlow.collect{
                if (foregroundIsActive) {
                    //todo replace in operation methods
                    notificationHelper.updateNotification(it)
                }
            }
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
                    stopSelf()
                }
            }
            else -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID", "Music Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        //onStopMusic()
        println("destroy 12342 ")
        customScope.coroutineContext.cancel()
        super.onDestroy()
    }

    private fun onPlayMusic(){
        val playingSongWithDetails = _currentSongFlow.value ?: return
        if (playingSongWithDetails.isPlaying) return
        musicController.playMusic(playingSongWithDetails.song.uri)
        musicController.setActionOnFinish {
            nextSong()
            updateSongState()
        }
        val newSong = _currentSongFlow.value
        _currentSongFlow.value = newSong?.copy(
            isPlaying = musicController.getIsPlayingMusicState(),
            currentPosition = musicController.getCurrentPosition())
    }

    private fun onPauseMusic(){
        val playingSongWithDetails = _currentSongFlow.value ?: return
        if(!playingSongWithDetails.isPlaying) return
        musicController.pauseMusic()
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
        val newSong = _currentSongFlow.value
        _currentSongFlow.value = newSong?.copy(
            isPlaying = musicController.getIsPlayingMusicState(),
            currentPosition = musicController.getCurrentPosition())
    }

    private fun pausePlaying(){
            musicController.pausePlaying()
    }

    private fun continuePlaying(){
            musicController.continuePlaying()
    }

    private val binder = GetServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class GetServiceBinder() : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    private fun getListAutoPlaySongs() = _autoPlayListSongs.value

    private fun getListSongs() = _listSongs.value

    fun getCurrentSongFlow(): Flow<SongWithDetails?> = _currentSongFlow

    fun setCurrentTime(currentTime: Int){
        musicController.setCurrentTimeInMillis(currentTime)
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
        val currentSong = _currentSongFlow.value?.song ?: throw IllegalStateException("Custom error Current song is null")
        val musicList = _listSongs.value ?: throw IllegalStateException("Custom error Music list is empty")
        val autoPlayList = _autoPlayListSongs.value ?: emptyList()

        val currentList = if (fromAutoPlayList) autoPlayList else musicList

        val currentIndex = currentList.indexOfFirst { it.uri == currentSong.uri }
        if (currentIndex == -1) throw IllegalStateException("Custom error Current song not found in list")

        if ((direction == 1 && currentIndex >= currentList.lastIndex) ||
            (direction == -1 && currentIndex <= 0)) throw IndexOutOfBoundsException("Custom error No more songs in this direction")

        val newIndex = currentIndex + direction

        val newSong = if (fromAutoPlayList) {
            findNextInAutoPlayList(musicList, autoPlayList, musicList[newIndex], direction)
        } else {
            musicList.getOrNull(newIndex) ?: throw IndexOutOfBoundsException("Custom error Invalid song index")
        }

        musicController.changeSong(newSong.uri)
        _currentSongFlow.value = musicController.getInformationForSong(newSong)
    }

    private fun findNextInAutoPlayList(
        listSongs: List<Song>,
        autoPlayList: List<Song>,
        target: Song,
        direction: Int
    ): Song {
        val indexInList = listSongs.indexOfFirst { it.uri == target.uri }
        if (indexInList == -1) throw IllegalStateException("Target song not found in main list")

        val nextAutoPlaySong = if (direction == 1) {
            listSongs.drop(indexInList + 1).firstOrNull { it in autoPlayList }
        } else {
            listSongs.take(indexInList).lastOrNull { it in autoPlayList }
        }

        return nextAutoPlaySong ?: throw NoSuchElementException("No next autoplay song found")
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