package com.example.fullproject.view

import android.net.Uri
import android.os.CountDownTimer
import com.example.fullproject.App
import com.example.fullproject.PlayerManager
import com.example.fullproject.businesslogic.SongMusic

class MusicPlayerViewModel(private val app: App) : BaseMusicViewModel(app) {
    var currentPosition: Long = 0
    var song: SongMusic = SongMusic(Uri.parse("Empty"))
    lateinit var manager: PlayerManager

    private lateinit var countTimer : CountDownTimer
    private var isTimerRun: Boolean = false

    fun onSoundPlay() {
        super.onSoundPlay(song)
        notifyDataChanged()
        startTimer()
    }

    fun startSound(){
        super.startSound(song)
        setTimeSound(getCurrentTime())
        startTimer()
        notifyDataChanged()
        }

    fun onSoundPause() {
        super.onSoundPause(song)
        stopTimer()
        notifyDataChanged()
    }

    fun onSoundStop() {
        super.onSoundStop(song)
        stopTimer()
        notifyDataChanged()
    }

    fun setTimeSound(progress: Long) {
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.setTimeSound(progress)
        notifyDataChanged()
    }

    fun pauseTimeSound() {
        super.pauseSound(song)
        stopTimer()
    }

    fun continueTimeSound() {
        super.continueSound(song)
        notifyDataChanged()
        startTimer()
    }
    fun previouslySound(){
        song = super.changeCurrentSong(song, -1)
        notifyDataChanged()
    }

    fun nextSound(){
        song = super.changeCurrentSong(song, 1)
        notifyDataChanged()
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
                notifyDataChanged()
            }
            override fun onFinish() {
                onSoundStop()
                notifyDataChanged()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    fun notifyDataChanged(){
        currentPosition = getCurrentTime()
        manager.updateStateElement()
    }
}