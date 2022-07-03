package com.example.fullproject.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import com.example.fullproject.MusicNavigator

typealias UpdateUI= () -> Unit

class SoundService(
    private var context: Context?,
    val playlist: MutableList<Song>,
    private val updateUI: UpdateUI
    ): MusicNavigator {

    private lateinit var countTimer : CountDownTimer
    private var mp: MediaPlayer? = null
    private var isTimerRun: Boolean = false

    var isPlay: Boolean = false
        private set
    var currentPositionInMillis: Long = 0
        private set
    var musicTimeInMillis: Long = 0
        private set

    override fun onSoundPlay(id: Int) {
        if(context != null){
            if (mp == null) {
                mp = MediaPlayer.create(context, id)
                musicTimeInMillis = (mp!!.duration).toLong()
            }
            startTimer()
            mp?.start()
            if (!isPlay) isPlay = !isPlay
            updateUI()
        }
    }

    override fun onSoundPause() {
        if (mp !== null) {
            stopTimer()
            mp?.pause()
            if (isPlay) isPlay = !isPlay
            updateUI()
        }
    }

    override fun onSoundStop() {
        if (mp !== null) {
            stopTimer()
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
            currentPositionInMillis = 0
            musicTimeInMillis = 0
            isPlay = false
            updateUI()
        }
    }

    fun setTimeSound(progress: Long){
        currentPositionInMillis = progress
        mp?.seekTo(progress.toInt())
        updateUI()
    }

    fun pauseTimeSound(){
        if (isPlay) {
            mp?.pause()
            stopTimer()
        }
    }

    fun continueTimeSound(){
        if (isPlay) {
            mp?.start()
            startTimer()
        }
    }

    private fun stopTimer(){
        if(!isTimerRun) return
        countTimer.cancel()
        isTimerRun = false
    }

    private fun startTimer(){
        if (isTimerRun) return
        countTimer = object : CountDownTimer(musicTimeInMillis - currentPositionInMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                currentPositionInMillis = (mp?.currentPosition ?: 0).toLong()
                updateUI()
            }
            override fun onFinish() {
                onSoundStop()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    fun updateCurrentPosition() {
        currentPositionInMillis = (mp?.currentPosition ?: 0).toLong()
    }

}
