package com.example.fullproject.view

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.fullproject.App
import com.example.fullproject.businesslogic.SongMusic

abstract class BaseMusicViewModel(private val app: App) : ViewModel() {

    fun onSoundPlay(context: Context, song: SongMusic){
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.onPlaySound(context, song)
        Log.d("onSoundPlay", "play")
    }

    fun onSoundPause(song: SongMusic){
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.onSoundPause(song)
        Log.d("onSoundPause", "pause")
    }

    fun onSoundStop(song: SongMusic){
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.onSoundStop(song)
        Log.d("onSoundStop", "stop")
    }

    fun pauseSound(song: SongMusic){
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.pauseTimeSound(song)
    }

    fun continueSound(song: SongMusic){
        //if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.continueTimeSound(song)
    }

    protected fun changeCurrentSong(context: Context, oldSong: SongMusic, moveBy: Int): SongMusic{
        if (uriNotCorrect(oldSong.uri)) return oldSong
        val newSong = app.soundServiceMusic.changeCurrentSong(oldSong, moveBy)
        if(oldSong == newSong) return oldSong
        onSoundStop(oldSong)
        startSound(context, newSong)
        return newSong
    }

    fun startSound(context: Context, song: SongMusic){
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.startSound(context, song)
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
}