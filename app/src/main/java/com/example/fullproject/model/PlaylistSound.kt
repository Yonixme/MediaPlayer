package com.example.fullproject.model

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import com.example.fullproject.tasks.MusicNavigator

typealias UPUI = () -> Unit

class PlaylistSound(
    private var context: Context?,
    val playlist: MutableList<Song>,
    private val updateUI: UPUI
    ): MusicNavigator{

    private lateinit var countTimer : CountDownTimer
    private var mp: MediaPlayer? = null
    private var isPlay: Boolean = false
    private var isTimerRun: Boolean = false

    var currentPositionInMillis: Int = 0
        private set
    var musicTimeInMillis: Int = 0
        private set

    override fun playSoundPlayer(id: Int) {
        if(context != null){
            if (mp == null) {
                mp = MediaPlayer.create(context, id)
            }
            startTimer()
            mp?.start()
            updateUI()
            if (!isPlay) isPlay = !isPlay
        }
    }

    override fun pauseSoundPlayer() {
        if (mp !== null) {
            stopTimer()
            mp?.pause()
        }
        if (isPlay) isPlay = !isPlay
    }

    override fun stopSoundPlayer() {
        if (mp !== null) {
            stopTimer()
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
            currentPositionInMillis = 0
            musicTimeInMillis = 0
            updateUI()
        }
    }

    fun setTimeSound(progress: Int){
        currentPositionInMillis = progress
        mp?.seekTo(progress)
        updateUI()
    }

    fun pauseTimeSound(){
        if (isPlay) mp?.pause()
        stopTimer()
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
        musicTimeInMillis = mp!!.duration
        if (isTimerRun) return
        countTimer = object : CountDownTimer((musicTimeInMillis - currentPositionInMillis).toLong(), 1000L){
            override fun onTick(millisUntilFinished: Long) {
                currentPositionInMillis = mp?.currentPosition ?: 0
                updateUI()
            }
            override fun onFinish() {
                stopSoundPlayer()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    override fun add(song: Song) {
        playlist.add(song)
    }


    override fun delete(song: Song) {
        playlist.remove(song)
    }
}
