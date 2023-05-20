package com.example.fullproject.screens.viewmodel

import android.net.Uri
import android.os.CountDownTimer
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.example.fullproject.App
import com.example.fullproject.R
import com.example.fullproject.screens.musicplayer.PlayerManager
import com.example.fullproject.model.Song
import kotlinx.coroutines.runBlocking

class MusicPlayerViewModel(private val app: App) : BaseMusicViewModel(app) {
    var manager: PlayerManager? = null
    private var showDataWithDB = true
    var song: Song = Song(Uri.parse("Empty"))
    private var listUriDB = mutableListOf<String>()
    private lateinit var countTimer : CountDownTimer
    private var isTimerRun: Boolean = false

    init {
        for (l in getSongsListWithDB()) listUriDB.add(l.uri)
    }

    fun onSoundPlay() {
        super.onSoundPlay(song)
        updateData()
        startTimerForUpdateUI()
    }

    override fun onSoundPause() {
        super.onSoundPause()
        stopTimerForUpdateUI()
        updateData()
    }

    override fun onSoundStop() {
        super.onSoundStop()
        if (uriNotCorrect(song)) {
            notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
            return
        }
        stopTimerForUpdateUI()
        if (manager != null) manager!!.updateViewUI()
    }

    fun setTimeSound(progress: Long) {
        if (uriNotCorrect(song)) return
        app.getMusicService().setTimeSound(progress)
        updateData()
    }

    fun pauseTimeSound() {
        super.pauseSound()
        stopTimerForUpdateUI()
    }

    fun continueTimeSound() {
        super.continueSound()
        startTimerForUpdateUI()
        updateData()
    }
    fun previouslySound(){
        super.previousSong()
        if (uriNotCorrect(song)) {
            notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
            return
        }
        song = app.getMusicService().currentSong
        updateData()
        if (app.getMusicService().isPlay) startTimerForUpdateUI()
    }

    fun nextSound(){
        super.nextSong()
        if (uriNotCorrect(song)) {
            notifyUser(app.applicationContext.resources.getString(R.string.music_not_found_alert))
            return
        }
        song = app.getMusicService().currentSong
        updateData()
        if (app.getMusicService().isPlay) startTimerForUpdateUI()
    }

    fun launchTimer(){
        if (song == app.getMusicService().currentSong) startTimerForUpdateUI()
    }

    override fun getCurrentPosition(): Long {
        if(song == app.getMusicService().currentSong) return super.getCurrentPosition()
        return 0L
    }

    fun updateData() = runBlocking{
        app.getMusicService().updateCurrentPosition()
        if (manager != null) manager!!.updateViewUI()
    }

    fun notifyUserUpdatedName(){
        notifyUser("Name was updated")
    }

    private fun checkSongInDB(): Boolean{
        if (song.uri.path.toString() in listUriDB) return true
        return false

    }

    fun getNameField(): String{
        var index = -1
        if (checkSongInDB() && showDataWithDB) {
            index = listUriDB.indexOf(song.uri.path.toString())
            return getSongsListWithDB()[index].name ?: song.uri.lastPathSegment.toString()
        }
        return song.uri.lastPathSegment.toString()
    }

    fun getAuthorField(): String{
        var index = -1
        if (checkSongInDB() && showDataWithDB) {
            index = listUriDB.indexOf(song.uri.path.toString())
            return getSongsListWithDB()[index].author ?: song.uri.lastPathSegment.toString()
        }
        return song.uri.lastPathSegment.toString()
    }

    fun onItemMoreClick(view: View){
        val popupMenu = PopupMenu(app.applicationContext, view)
        popupMenu.menu.add(0, SET_NAME_ID, Menu.NONE, "set name for music")

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                SET_NAME_ID -> {
                    showDataWithDB = !showDataWithDB
                    updateData()
                    notifyUserUpdatedName()
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun startTimerForUpdateUI(){
        if (isTimerRun) return
        countTimer = object : CountDownTimer(
            app.getMusicService().duration - app.getMusicService().currentTime,
            500L) {
            override fun onTick(millisUntilFinished: Long) {
                if (app.getMusicService().isAudioFocusLose) {
                    stopTimerForUpdateUI()
                    startTimerAwait {
                        startTimerForUpdateUI()
                    }
                }
                else updateData()
            }
            override fun onFinish() {
                stopTimerForUpdateUI()
                updateData()
                val index = getListSong().indexOf(song)
                if (index < getListSong().size - 1) nextSound()
                else onSoundStop()
                updateData()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    private fun stopTimerForUpdateUI(){
        if(!isTimerRun) return
        countTimer.cancel()
        isTimerRun = false
    }

    override fun onCleared() {
        super.onCleared()
        manager = null
        stopTimerForUpdateUI()
    }

    companion object{
        private const val SET_NAME_ID = 1
    }
}