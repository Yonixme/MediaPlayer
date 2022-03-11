package com.example.fullproject.model

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.fullproject.tasks.MusicNavigator
import java.lang.Exception

typealias UPUI = () -> Unit

class PlaylistSound(
    private var context: Context?,
    val playlist: MutableList<Song>,
    private val updateUI: UPUI
    ): MusicNavigator{

    private var mp: MediaPlayer? = null
    private var isPlay: Boolean = true
    var currentPosition: Int = 0
    var musicTime: Int = 0

    override fun playSoundPlayer(id: Int) {
        Log.d("MainActivity", "playSoundPlayer sssssss")
        if(context != null){
            if (mp == null) {
                mp = MediaPlayer.create(context, id)
                Log.d("MainActivity", "playSoundPlayer")
                startTimer()
            }
            mp?.start()
            updateUI()
            if (!isPlay) isPlay = !isPlay
        }
    }

    override fun pauseSoundPlayer() {
        if (mp !== null) {
            mp?.pause()
        }
        if (isPlay) isPlay = !isPlay
        Log.d("MainActivity", "pauseSoundPlayer")
    }

    override fun stopSoundPlayer() {
        if (mp !== null) {
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
        }
        updateUI()
        Log.d("MainActivity", "stopSoundPlayer")
    }

    fun playSound(progress: Int){
        currentPosition = progress
        mp?.seekTo(progress)
        updateUI()
        Log.d("MainActivity", "playSound${progress}")
    }

    fun pauseSound(){
        if (isPlay) mp?.pause()
        Log.d("MainActivity", "pauseSound")
    }

    fun continueSound(){
        if (isPlay) mp?.start()
        Log.d("MainActivity", "continueSound")
    }

    private fun startTimer() {
        musicTime = mp!!.duration
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    currentPosition = mp?.currentPosition ?: 0
                    Log.d("MainActivity", "handler.postDelayed(object : Runnable ${mp?.currentPosition}")
                    updateUI()
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    currentPosition = 0
                    updateUI()
                }
            }
        }, 0)
    }

    override fun add(song: Song) {
        playlist.add(song)
    }


    override fun delete(song: Song) {
        playlist.remove(song)
    }
}
