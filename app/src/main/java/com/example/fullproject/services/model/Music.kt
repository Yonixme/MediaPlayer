package com.example.fullproject.services.model

import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.App
import com.example.fullproject.businesslogic.equalsWithSupportedFormat
import com.example.fullproject.businesslogic.getFormatFile
import java.io.File

class Music(private val application: App) {
    private var mp: MediaPlayer? = null
    private var currentSong: Song = SongMapper.Base(Uri.parse(" ")).map()
    private var lastSong: Song = SongMapper.Base(Uri.parse(" ")).map()

    var isPlay = false
        private set
    var duration = 0L
        private set
    var currentTime = 0L
        private set
    var songs = mutableListOf<Song>()
        private set

    init {
        updateData()
    }

    private fun createMusic(song: Song){
        if (currentSong == song) return
        lastSong = if (mp == null) song else currentSong
        currentSong = song
        mp = MediaPlayer.create(application.applicationContext, song.uri)
        duration = (mp!!.duration).toLong()
    }

    private fun startSound(song: Song) {
        createMusic(song)
        if(isPlay) mp?.start()

    }

    fun onPlay(song: Song){
        startSound(song)
        mp?.start()
        isPlay = true
    }

    fun onStop(song: Song){
        mp?.stop()
        mp?.reset()
        mp?.release()
        currentTime = 0L
    }

    fun updateData(){
        updateListSongs()
    }

    private fun updateListSongs(){
        val listOFMusic = mutableListOf<File>()
        val uris = mutableListOf<Song>()

        val file1 = File("/storage/emulated/0/Download")
        val file2 = File("/storage/emulated/0/Music")
        if (file1.isDirectory && file1.listFiles() != null) listOFMusic.addAll(file1.listFiles()!!)
        if (file2.isDirectory && file2.listFiles() != null) listOFMusic.addAll(file2.listFiles()!!)

        for (u in listOFMusic) {
            if (equalsWithSupportedFormat(getFormatFile(u.name)))
                uris.add(SongMapper.Base(Uri.fromFile(u)).map())
        }

        if (uris == songs) return
        songs = uris
    }
}