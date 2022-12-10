package com.example.fullproject.view

import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import com.example.fullproject.App
import com.example.fullproject.NavigatorPlaylist
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.view.BaseMusicViewModel

class MusicPlayerViewModel(private val app: App) : BaseMusicViewModel(app) {
    var currentPosition: Long = 0
    var song: SongMusic = SongMusic(Uri.parse("Empty"))
    lateinit var navigator: NavigatorPlaylist

    private lateinit var countTimer : CountDownTimer
    private var isTimerRun: Boolean = false

    fun onSoundPlay(context: Context) {
        super.onSoundPlay(context, song)
        notifyDataChanged()
        startTimer()
    }

    fun startSound(context: Context){
        super.startSound(context, song)
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
    fun previouslySound(context: Context){
        song = super.changeCurrentSong(context, song, -1)
        notifyDataChanged()
    }

    fun nextSound(context: Context){
        song = super.changeCurrentSong(context, song, 1)
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
            getCurrentTime() - app.soundServiceMusic.currentPositionInMillis,
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
        navigator.updateUI()
    }
}