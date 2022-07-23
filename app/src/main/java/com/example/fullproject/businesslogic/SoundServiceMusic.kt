package com.example.fullproject.businesslogic

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.tasks.equalsWithSupportFormat
import com.example.fullproject.tasks.getFormatFile
import java.io.File

class SoundServiceMusic() {
    private var mp: MediaPlayer? = null

    var currentPositionInMillis: Long = 0
        private set
    var musicTimeInMillis: Long = 0
        private set

    val songs= mutableListOf<Uri>()

    init {
        updateList()
    }

    fun playSound(context: Context, songMusic: SongMusic){
            if (mp == null) {
                mp = MediaPlayer.create(context, songMusic.uri)
                musicTimeInMillis = (mp!!.duration).toLong()
            }
            mp?.start()
            songMusic.isPlay = true
    }

    fun soundPause(songMusic: SongMusic) {
        if (mp !== null) {
            mp?.pause()
            songMusic.isPlay = false
        }
    }

    fun soundStop(songMusic: SongMusic) {
        if (mp !== null) {
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
            currentPositionInMillis = 0
            musicTimeInMillis = 0
            songMusic.isPlay = false
        }
    }

    fun setTimeSound(progress: Long){
        currentPositionInMillis = progress
        mp?.seekTo(progress.toInt())
    }

    fun pauseTimeSound(songMusic: SongMusic){
        if (songMusic.isPlay) {
            mp?.pause()
        }
    }

    fun continueTimeSound(songMusic: SongMusic){
        if (songMusic.isPlay) {
            mp?.start()
        }
    }

    fun updateCurrentPosition() {
        currentPositionInMillis = (mp?.currentPosition ?: 0).toLong()
    }

    fun updateList(){
        val songList = songs
        songs.removeAll(songList)
        val listOFMusic = mutableListOf<File>()
        val uris = mutableListOf<Uri>()

        val file1 = File("/storage/emulated/0/Download")
        val file2 = File("/storage/emulated/0/Music")
        if (file1.isDirectory && file1.listFiles() != null) listOFMusic.addAll(file1.listFiles()!!)
        if (file2.isDirectory && file2.listFiles() != null) listOFMusic.addAll(file2.listFiles()!!)

        for (u in listOFMusic) {
            if (equalsWithSupportFormat(getFormatFile(u.toString())))
                uris.add(Uri.fromFile(u))
        }
        songs.addAll(uris)
    }
}