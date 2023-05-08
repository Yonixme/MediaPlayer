package com.example.fullproject.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.App
import com.example.fullproject.R
import com.example.fullproject.Repositories
import com.example.fullproject.services.model.songpack.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class BaseMusicViewModel(private val app: App) : ViewModel() {

    open fun onSoundPlay(song: Song){
        if (uriNotCorrect(song)) {
            notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
            return
        }
        app.getMusicService().onPlay(song)
        Log.d("onSoundPlay", "play")
    }

    open fun onSoundPause(): Unit = runBlocking{
        app.getMusicService().onSoundPause()
        Log.d("onSoundPause", "pause")
    }

    open fun onSoundStop(): Unit = runBlocking{
        app.getMusicService().onStop()
        Log.d("onSoundStop", "stop")
    }

    open fun pauseSound() = runBlocking{
        app.getMusicService().pauseTimeSound()
    }

    open fun continueSound() = runBlocking{
        app.getMusicService().continueTimeSound()
    }

    open fun nextSong(){
        app.getMusicService().nextSound()
        Log.d("Debug123", "next in VM")
    }

    open fun previousSong(){
        app.getMusicService().previousSound()
        Log.d("Debug123", "prev in VM")
    }

    fun uriNotCorrect(song: Song): Boolean {
        return song !in getListSong()
    }

    open fun getCurrentPosition(): Long = runBlocking {
        return@runBlocking app.getMusicService().currentTime
    }

    fun getDuration(): Long = runBlocking {
        return@runBlocking app.getMusicService().duration
    }

     fun getListSong(): List<Song> = runBlocking{
         app.getMusicService().updateData()
         return@runBlocking app.getMusicService().songs
    }

    fun isPlaySound(): Boolean = runBlocking {
        app.getMusicService().isPlay
    }

    fun getCurrentSong(): Song = runBlocking {
        return@runBlocking app.getMusicService().currentSong
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
}