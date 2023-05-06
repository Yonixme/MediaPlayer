package com.example.fullproject.services.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.businesslogic.equalsWithSupportedFormat
import com.example.fullproject.businesslogic.getFormatFile
import java.io.File

class SoundServiceMusic{
    private var mp: MediaPlayer? = null

    var currentPositionInMillis: Long
        private set
    var musicTimeInMillis: Long
        private set

    var songs = mutableListOf<Uri>()

    var musicList = mutableListOf<SongMusic>()
        private set

    init {
        currentPositionInMillis = 0
        musicTimeInMillis = 0
        updateList()
    }

    fun onPlaySound(context: Context, songMusic: SongMusic){
        createMusic(context, songMusic)
        mp?.start()
        songMusic.isPlay = true
    }

    fun startSound(context: Context, songMusic: SongMusic) {
        createMusic(context, songMusic)
        if(songMusic.isPlay)
            mp?.start()
    }

    fun onSoundPause(songMusic: SongMusic) {
        if (mp == null) return
        mp?.pause()
        songMusic.isPlay = false
    }

    fun onSoundStop(songMusic: SongMusic) {
        if (mp == null) return
        mp?.stop()
        mp?.reset()
        mp?.release()
        mp = null
        currentPositionInMillis = 0
        songMusic.isPlay = false
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

    fun changeCurrentSong(song: SongMusic, moveBy: Int): SongMusic {
        val isPlay = song.isPlay
        val oldMusicIndex = songs.indexOfFirst { song.uri == it }
        if (oldMusicIndex == -1) return song
        val newMusicIndex = oldMusicIndex + moveBy
        if (newMusicIndex < 0 || newMusicIndex == songs.size) return song
        return SongMusic(songs[newMusicIndex], isPlay)
    }

    fun updateCurrentPosition() {
        currentPositionInMillis = (mp?.currentPosition ?: 0).toLong()
    }

    fun updateList(){
        val listOFMusic = mutableListOf<File>()
        val uris = mutableListOf<Uri>()

        val file1 = File("/storage/emulated/0/Download")
        val file2 = File("/storage/emulated/0/Music")
        val file3 = File("/storage/emulated/0/Ringtone")
        if (file1.isDirectory && file1.listFiles() != null) listOFMusic.addAll(file1.listFiles()!!)
        if (file2.isDirectory && file2.listFiles() != null) listOFMusic.addAll(file2.listFiles()!!)
        if (file2.isDirectory && file3.listFiles() != null) listOFMusic.addAll(file3.listFiles()!!)

        for (u in listOFMusic) {
            if (equalsWithSupportedFormat(getFormatFile(u.name)))
                uris.add(Uri.fromFile(u))
        }
        if(songs == uris) return
        songs = uris
        for (uri in songs)
            musicList.add(SongMusic(uri))
    }

    private fun createMusic(context: Context, songMusic: SongMusic){
        if (mp !== null) return
        mp = MediaPlayer.create(context, songMusic.uri)
        musicTimeInMillis = (mp!!.duration).toLong()
    }
}

