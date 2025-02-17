package com.example.fullproject.model.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.fullproject.model.services.MusicServiceManager.CurrentSongState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicServiceManager {
    private val customScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val _currentSong: MutableStateFlow<CurrentSongState> = MutableStateFlow(
        CurrentSongState.Loading)
    val currentSongNew: Flow<CurrentSongState> = _currentSong

    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.GetServiceBinder
            musicService = binder.getService()
            isBound = true

            customScope.launch {
                println("check service $musicService")
                musicService?.getCurrentSongFlow()?.collect{
                    songFromFlow ->
                    _currentSong.value = if (songFromFlow != null){
                        CurrentSongState.Success(songFromFlow)
                    }else{
                        CurrentSongState.Loading
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    init {
        bindService()
    }

    override fun bindService(){
        Intent(context, MusicService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unBindService(){
        println("Debug22 in manager $this")
        if (isBound){
            println("Debug22 in manager $this")
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun getCurrentSongWithDetails(): Flow<CurrentSongState?> = _currentSong

    override fun onPlay(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_ON_PLAY_MUSIC,
            uri = uri
        )
    }

    override fun onPause(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_ON_PAUSE_MUSIC,
            uri = uri
        )
    }

    override fun onStop(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_ON_STOP_MUSIC,
            uri = uri
        )
    }

    override fun pauseMusic(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_PAUSE_PLAYING,
            uri = uri
        )
    }

    override fun continueMusic(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_CONTINUE_PLAYING,
            uri = uri
        )
    }

    override fun nextSong(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_NEXT_SONG,
            uri = uri
        )
    }

    override fun previousSong(uri: String) {
        startServiceWithCommand(
            action = MusicService.COMMAND_PREVIOUS_SONG,
            uri = uri
        )
    }

    private fun startServiceWithCommand(action: String, uri: String){
        println("Manager123: $action uri = $uri")
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        intent.putExtra(MusicService.ID_URI, uri)
        context.startService(intent)
    }

    override fun getIsPlaying(): Boolean {
        return musicService?.getIsPlayingState() ?: false
    }

    override fun setCurrentTime(currentTime: Int) {
        musicService?.setCurrentTime(currentTime)
    }

    override fun updateSongState() {
        musicService?.updateSongState()
    }


}