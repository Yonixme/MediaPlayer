package com.example.fullproject.screens.musicplayer

import android.net.Uri
import android.os.CountDownTimer
import com.example.fullproject.App
import com.example.fullproject.PlayerManager
import com.example.fullproject.services.model.SongMusic
import com.example.fullproject.screens.BaseMusicViewModel

class MusicPlayerViewModel(private val app: App) : BaseMusicViewModel(app) {
    var currentPosition: Long = 0
    var song: SongMusic = SongMusic(Uri.parse("Empty"))
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

    fun startSound(){
        super.startSound(song)
        setTimeSound(getCurrentTime())
        startTimer()
        updateData()
        }

    fun onSoundPause() {
        super.onSoundPause(song)
        stopTimer()
        updateData()
    }

    fun onSoundStop() {
        super.onSoundStop(song)
        stopTimer()
        updateData()

    }



    fun setTimeSound(progress: Long) {
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.setTimeSound(progress)
        updateData()
    }

    fun pauseTimeSound() {
        super.pauseSound(song)
        stopTimer()
    }

    fun continueTimeSound() {
        super.continueSound(song)
        updateData()
        startTimer()
    }
    fun previouslySound(){
        song = super.changeCurrentSong(song, -1)
        updateData()
    }

    fun nextSound(){
        song = super.changeCurrentSong(song, 1)
        updateData()
    }

    private fun stopTimer(){
        if(!isTimerRun) return
        countTimer.cancel()
        isTimerRun = false
    }

    private fun startTimer(){
        if (isTimerRun) return
        countTimer = object : CountDownTimer(
            app.soundServiceMusic.musicTimeInMillis - app.soundServiceMusic.currentPositionInMillis,
            1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateData()
            }
            override fun onFinish() {
                onSoundStop()
                updateData()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    fun updateData(){
        currentPosition = getCurrentTime()
        manager.updateStateElement()
    }
}








