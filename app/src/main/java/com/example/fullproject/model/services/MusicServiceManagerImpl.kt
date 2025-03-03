package com.example.fullproject.model.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.fullproject.model.services.MusicServiceManager.CurrentSongState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicServiceManager {
    private val customScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _currentSong: MutableStateFlow<CurrentSongState> = MutableStateFlow(
        CurrentSongState.Loading)

    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.GetServiceBinder
            musicService = binder.getService()
            isBound = true
            customScope.launch {
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
            println("debug flow111 disconnect")
            musicService = null
            isBound = false
            customScope.coroutineContext.cancelChildren()
        }
    }

    override fun bindService(){
        Intent(context, MusicService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unBindService(){
        if (isBound){
            context.unbindService(serviceConnection)
            isBound = false
            musicService = null
        }
    }

    override fun getCurrentSongWithDetails(): Flow<CurrentSongState> = _currentSong

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
        unBindService()
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
        if(musicService == null) bindService()
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        intent.putExtra(MusicService.ID_URI, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun setCurrentTime(currentTime: Int) {
        if(musicService == null) bindService()
        musicService?.setCurrentTime(currentTime)
    }

    override fun updateSongState() {
        if(musicService == null) bindService()
        musicService?.updateSongState()
    }


}