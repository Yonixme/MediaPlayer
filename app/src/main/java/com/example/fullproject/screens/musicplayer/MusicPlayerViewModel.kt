package com.example.fullproject.screens.musicplayer

import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import com.example.fullproject.App
import com.example.fullproject.PlayerManager
import com.example.fullproject.screens.BaseMusicViewModel
import com.example.fullproject.services.model.songpack.entities.Song
import kotlinx.coroutines.runBlocking

class MusicPlayerViewModel(private val app: App) : BaseMusicViewModel(app) {

    var song: Song = Song(Uri.parse("Empty"))
    lateinit var manager: PlayerManager

    // Create logic for any SDK version

    // Created logic for any SDK version

    private lateinit var countTimer : CountDownTimer
    private var isTimerRun: Boolean = false


    fun onSoundPlay() {
        super.onSoundPlay(song)
        updateData()
        startTimer()
    }

    override fun onSoundPause() {
        super.onSoundPause()
        stopTimer()
        updateData()
    }

    override fun onSoundStop() {
        super.onSoundStop()
        stopTimer()
        updateData()
    }

    fun setTimeSound(progress: Long) {
        if (uriNotCorrect(song)) return
        app.getMusicService().setTimeSound(progress)
        updateData()
    }

    fun pauseTimeSound() {
        super.pauseSound()
        stopTimer()
    }

    fun continueTimeSound() {
        super.continueSound()
        updateData()
        startTimer()
    }
    fun previouslySound(){
        super.previousSong()
        song = app.getMusicService().currentSong
        if (app.getMusicService().isPlay) startTimer()
        updateData()
    }

    fun nextSound(){
        super.nextSong()
        song = app.getMusicService().currentSong
        if (app.getMusicService().isPlay) startTimer()
        updateData()
    }

    private fun stopTimer(){
        if(!isTimerRun) return
        countTimer.cancel()
        isTimerRun = false
    }

    fun launchTimer(){
        if (song == app.getMusicService().currentSong) startTimer()
    }

    override fun getCurrentPosition(): Long {
        if(song == app.getMusicService().currentSong) return super.getCurrentPosition()
        return 0L
    }

    private fun startTimer(){
        if (isTimerRun) return
        countTimer = object : CountDownTimer(
            app.getMusicService().duration - app.getMusicService().currentTime,
            1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateData()
            }
            override fun onFinish() {
                nextSound()
                updateData()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    fun updateData() = runBlocking{
        app.getMusicService().updateCurrentPosition()
        manager.updateViewUI()
    }

    fun notifyUserWhatElementAddedLater(){
        notifyUser("This Element will be added Later")
    }
}