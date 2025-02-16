package com.example.fullproject.screens.viewmodels.oldr

import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.fullproject.App
import com.example.fullproject.R
import com.example.fullproject.model.songpack.entities.MetaDataSong
import com.example.fullproject.model.songpack.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

abstract class BaseMusicViewModelOLD(private val app: App) : ViewModel() {
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

     fun getListSong(): List<Song> = runBlocking(Dispatchers.IO){
             app.getMusicService().updateData()
             return@runBlocking app.getMusicService().songs
    }

    fun isPlaySound(): Boolean {
        return app.getMusicService().isPlay
    }

    fun getCurrentSong(): Song = runBlocking {
        app.getMusicService().currentSong
    }

    fun getSongsListWithDB(): List<MetaDataSong> = runBlocking{
        return@runBlocking BaseListViewModelOLD.Base().getListSongWithDB(false)
    }

    open fun notifyUser(outputText: String? = null){
        if (outputText != null){
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