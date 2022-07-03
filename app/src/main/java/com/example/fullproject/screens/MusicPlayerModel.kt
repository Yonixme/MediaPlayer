package com.example.fullproject.screens

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fullproject.model.SoundService
import com.example.fullproject.model.Song

typealias UpdateUI = () -> Unit

class MusicPlayerModel(
    val soundService: SoundService,
    val updateUI: UpdateUI
) : ViewModel(){
    private lateinit var countTimer : CountDownTimer

    private var isTimerRun: Boolean = false

    fun onSoundPlay(id: Int) {
        soundService.onSoundPlay(id)
        updateUI()
    }

    fun onSoundPause() {
        soundService.onSoundPause()
        updateUI()
    }

    fun onSoundStop() {
        soundService.onSoundStop()
        updateUI()
    }

    fun setTimeSound(progress: Int) {
        soundService.setTimeSound(progress.toLong())
        updateUI()
    }

    fun pauseTimeSound() {
        soundService.pauseTimeSound()
        updateUI()
    }

    fun continueTimeSound() {
        soundService.continueTimeSound()
        updateUI()
    }

    private fun stopTimer(){
        if(!isTimerRun) return
        countTimer.cancel()
        isTimerRun = false
    }

    private fun startTimer(){
        if (isTimerRun) return
        countTimer = object : CountDownTimer(
            soundService.musicTimeInMillis - soundService.currentPositionInMillis,
            1000L
        )
        {
            override fun onTick(millisUntilFinished: Long) {
                soundService.updateCurrentPosition()
                updateUI()
            }
            override fun onFinish() {
                onSoundStop()
                updateUI()
            }
        }
        countTimer.start()
        isTimerRun = true
    }
}