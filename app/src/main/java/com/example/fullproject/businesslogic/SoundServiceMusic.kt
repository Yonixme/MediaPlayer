package com.example.fullproject.businesslogic

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class SoundServiceMusic(
    private val listOFMusic: ArrayList<Uri>?,
) {
    private var context:Context? = null
    private var mp: MediaPlayer? = null

    var currentSong: Uri = listOFMusic!![0]
        private set
    var isPlay: Boolean = false
        private set
    var currentPositionInMillis: Long = 0
        private set
    var musicTimeInMillis: Long = 0
        private set

    fun putContext(context: Context?){
        this.context = context
    }

    fun playSound(){
        if(context != null)
        {
            if (mp == null) {
                mp = MediaPlayer.create(context, currentSong)
                musicTimeInMillis = (mp!!.duration).toLong()
            }
            mp?.start()
            if (!isPlay) isPlay = !isPlay
        }
    }

    fun soundPause() {
        if (mp !== null) {
            mp?.pause()
            if (isPlay) isPlay = !isPlay
        }
    }

    fun soundStop() {
        if (mp !== null) {
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
            currentPositionInMillis = 0
            musicTimeInMillis = 0
            isPlay = false
        }
    }

    fun setTimeSound(progress: Long){
        currentPositionInMillis = progress
        mp?.seekTo(progress.toInt())
    }

    fun pauseTimeSound(){
        if (isPlay) {
            mp?.pause()
        }
    }

    fun continueTimeSound(){
        if (isPlay) {
            mp?.start()
        }
    }

    fun updateCurrentPosition() {
        currentPositionInMillis = (mp?.currentPosition ?: 0).toLong()
    }
}