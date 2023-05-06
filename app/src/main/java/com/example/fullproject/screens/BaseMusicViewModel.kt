package com.example.fullproject.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.fullproject.App
import com.example.fullproject.R
import com.example.fullproject.services.model.SongMusic

abstract class BaseMusicViewModel(private val app: App) : ViewModel() {

    fun onSoundPlay(song: SongMusic){
        if (uriNotCorrect(song.uri)) notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
        app.soundServiceMusic.onPlaySound(app.applicationContext, song)
        Log.d("onSoundPlay", "play")
    }

    fun onSoundPause(song: SongMusic){
        if (uriNotCorrect(song.uri)) notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
        app.soundServiceMusic.onSoundPause(song)
        Log.d("onSoundPause", "pause")
    }

    fun onSoundStop(song: SongMusic){
        if (uriNotCorrect(song.uri)) notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
        app.soundServiceMusic.onSoundStop(song)
        Log.d("onSoundStop", "stop")
    }

    fun pauseSound(song: SongMusic){
        if (uriNotCorrect(song.uri)) notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
        app.soundServiceMusic.pauseTimeSound(song)
    }

    fun continueSound(song: SongMusic){
        //if (uriNotCorrect(song.uri)) onError(app.applicationContext.resources.getString(R.string.music_not_found_alert))
        app.soundServiceMusic.continueTimeSound(song)
    }

    protected fun changeCurrentSong(oldSong: SongMusic, moveBy: Int): SongMusic {
        if (uriNotCorrect(oldSong.uri)) return oldSong
        val newSong = app.soundServiceMusic.changeCurrentSong(oldSong, moveBy)
        if(oldSong == newSong) return oldSong
        onSoundStop(oldSong)
        startSound(newSong)
        return newSong
    }

    fun startSound(song: SongMusic){
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.startSound(app.applicationContext, song)
    }

    fun getCurrentTime(): Long {
        app.soundServiceMusic.updateCurrentPosition()
        return app.soundServiceMusic.currentPositionInMillis
    }

    fun getDuration() = app.soundServiceMusic.musicTimeInMillis

    fun uriNotCorrect(uri: Uri): Boolean = uri !in getListSong()

    fun getListSong(): MutableList<Uri>{
        app.soundServiceMusic.updateList()
        val list = mutableListOf<Uri>()
        for(u in app.soundServiceMusic.songs)
            list.add(u)
        return list
    }

    private fun notifyUser(outputText: String? = null){
        if (outputText !== null){
            Toast.makeText(app.applicationContext, outputText, Toast.LENGTH_SHORT).show()
        }
    }

    open fun onError(notifyUser: String? = null){
        notifyUser(notifyUser)
    }
}