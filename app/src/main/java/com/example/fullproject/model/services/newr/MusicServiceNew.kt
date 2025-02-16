package com.example.fullproject.model.services.newr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fullproject.R
import com.example.fullproject.model.room.song.MusicRepository
import com.example.fullproject.model.room.song.controller.MusicController
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails
import com.example.fullproject.model.room.song.infoprovider.MusicInfoProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicServiceNew : Service(){
    @Inject lateinit var musicController: MusicController
    @Inject lateinit var musicRepository: MusicRepository
    @Inject lateinit var musicInfoProvider: MusicInfoProvider

    private val customScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val _currentSongFlow: MutableStateFlow<SongWithDetails?> = MutableStateFlow(null)

    private val _autoPlayListSongs: MutableStateFlow<List<SongNew>?> = MutableStateFlow(null)
    private val _listSongs: MutableStateFlow<List<SongNew>?> = MutableStateFlow(null)

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
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Music Player")
            .setContentText("Playing...")
            .setSmallIcon(R.drawable.music_image)
            .build()
        startForeground(1, notification)
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


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uri = intent?.getStringExtra(ID_URI) ?: return START_STICKY
        println("debug123 in service $uri")
        if (_currentSongFlow.value?.song?.uri != uri) {
            val lastSong = _currentSongFlow.value
            onStopMusic()
            val newSong = musicRepository.getSongByURI(uri) ?: return START_STICKY
            _currentSongFlow.value = musicInfoProvider.getInformationForSong(newSong)
        }

        when (intent.action) {
            COMMAND_ON_PLAY_MUSIC -> onPlayMusic()
            COMMAND_ON_PAUSE_MUSIC -> onPauseMusic()
            COMMAND_ON_STOP_MUSIC -> onStopMusic()
            COMMAND_CONTINUE_PLAYING -> continuePlaying()
            COMMAND_PAUSE_PLAYING -> pausePlaying()
            COMMAND_NEXT_SONG -> nextSong()
            COMMAND_PREVIOUS_SONG -> previousSong()
            else -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT < 34)
            stopForeground(true)
        else
            stopForeground(STOP_FOREGROUND_REMOVE)
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
        if(!musicController.getIsPlayingMusicState()) return
        musicController.pauseMusic()
        val newSong = _currentSongFlow.value
        _currentSongFlow.value = newSong?.copy(
            isPlaying = musicController.getIsPlayingMusicState(),
            currentPosition = musicController.getCurrentPosition())
    }

    private fun onStopMusic(){
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

    //Todo delete method
    fun getIsPlayingState(): Boolean{
        return musicController.getIsPlayingMusicState()
    }

    private val binder = GetServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class GetServiceBinder() : Binder() {
        fun getService(): MusicServiceNew {
            return this@MusicServiceNew
        }
    }

    private fun getListAutoPlaySongs() = _listSongs.value

    private fun getListSongs() = _autoPlayListSongs.value

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

    // Переписано з винятками
    private fun changeCurrentSong(direction: Int, fromAutoPlayList: Boolean) {
        val currentSong = _currentSongFlow.value?.song ?: throw IllegalStateException("Current song is null")
        val musicList = _listSongs.value ?: throw IllegalStateException("Music list is empty")
        val autoPlayList = _autoPlayListSongs.value ?: emptyList()

        val currentList = if (fromAutoPlayList) autoPlayList else musicList

        val currentIndex = currentList.indexOfFirst { it.uri == currentSong.uri }
        if (currentIndex == -1) throw IllegalStateException("Current song not found in list")

        if ((direction == 1 && currentIndex >= currentList.lastIndex) ||
            (direction == -1 && currentIndex <= 0)) throw IndexOutOfBoundsException("No more songs in this direction")

        val newIndex = currentIndex + direction

        val newSong = if (fromAutoPlayList) {
            findNextInAutoPlayList(musicList, autoPlayList, musicList[newIndex], direction)
        } else {
            musicList.getOrNull(newIndex) ?: throw IndexOutOfBoundsException("Invalid song index")
        }

        musicController.changeSong(newSong.uri)
        _currentSongFlow.value = musicController.getInformationForSong(newSong)
    }

    private fun findNextInAutoPlayList(
        listSongs: List<SongNew>,
        autoPlayList: List<SongNew>,
        target: SongNew,
        direction: Int
    ): SongNew {
        val indexInList = listSongs.indexOfFirst { it.uri == target.uri }
        if (indexInList == -1) throw IllegalStateException("Target song not found in main list")

        val nextAutoPlaySong = if (direction == 1) {
            listSongs.drop(indexInList + 1).firstOrNull { it in autoPlayList }
        } else {
            listSongs.take(indexInList).lastOrNull { it in autoPlayList }
        }

        return nextAutoPlaySong ?: throw NoSuchElementException("No next autoplay song found")
    }

    private fun findSongFromListByUri(uri: String): SongNew? {
        return getListSongs()?.first{s -> s.uri == uri}
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