package com.example.fullproject.screens.viewmodel

import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.fullproject.App
import com.example.fullproject.R
import com.example.fullproject.model.Song
import kotlinx.coroutines.runBlocking

abstract class BaseMusicViewModel(private val app: App) : ViewModel() {
    var timerAwaitAudioFocus: CountDownTimer? = null

    open fun onSoundPlay(song: Song){
        if (uriNotCorrect(song)) {
            notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
            return
        }
        app.getMusicService().onPlay(song)
    }

    open fun onSoundPause(){
        app.getMusicService().onSoundPause()
    }

    open fun onSoundStop(){
        app.getMusicService().onStop()
    }

    open fun pauseSound() {
        app.getMusicService().pauseTimeSound()
    }

    open fun continueSound(){
        app.getMusicService().continueTimeSound()
    }

    open fun nextSong(){
        app.getMusicService().nextSound()
        Log.d("ddddddd", "app.getMusicService().isPlay.toString()")
    }

    open fun previousSong(){
        app.getMusicService().previousSound()
    }

    fun uriNotCorrect(song: Song): Boolean {
        return song !in getListSong()
    }

    open fun getCurrentPosition(): Long {
        return app.getMusicService().currentTime
    }

    fun getDuration(): Long{
        return app.getMusicService().duration
    }

     fun getListSong(): List<Song> {
         app.getMusicService().updateData()
         return app.getMusicService().songs
    }

    fun isPlaySound(): Boolean {
        return app.getMusicService().isPlay
    }

    fun getCurrentSong(): Song {
        return app.getMusicService().currentSong
    }

    fun getSongsListWithDB() = runBlocking {
        return@runBlocking BaseListViewModel.Base().getListSongWithDB(false)
    }

    open fun notifyUser(outputText: String? = null){
        if (outputText !== null){
            Toast.makeText(app.applicationContext, outputText, Toast.LENGTH_SHORT).show()
        }
    }

    open fun onError(notifyUser: String? = null){
        notifyUser(notifyUser)
    }

    fun startTimerAwait(timer: ()->Unit){
        if (timerAwaitAudioFocus != null) return

        timerAwaitAudioFocus = object : CountDownTimer(
            1000000L,
            100L) {
            override fun onTick(millisUntilFinished: Long) {
                if (!app.getMusicService().isAudioFocusLose) {
                    stopTimerAwait()
                    timer()
                }
            }
            override fun onFinish() {
                stopTimerAwait()
                startTimerAwait(timer)
            }
        }
        timerAwaitAudioFocus!!.start()
    }

    fun stopTimerAwait(){
        if (timerAwaitAudioFocus == null) return
        timerAwaitAudioFocus!!.cancel()
        timerAwaitAudioFocus = null
    }
}