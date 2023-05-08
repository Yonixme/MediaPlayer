package com.example.fullproject.services.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.fullproject.Repositories
import com.example.fullproject.businesslogic.equalsWithSupportedFormat
import com.example.fullproject.businesslogic.getFormatFile
import com.example.fullproject.services.model.dirpack.entities.Dir
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import com.example.fullproject.services.model.songpack.entities.Song
import kotlinx.coroutines.*
import java.io.File

class SoundServiceMusic(private var context: Context){
    private var mp: MediaPlayer? = null

    var currentPositionInMillis: Long
        private set
    var musicTimeInMillis: Long
        private set

    var songs = mutableListOf<Uri>()

    var musicList = mutableListOf<SongMusic>()
        private set

    init {
        Repositories.init(context)
        currentPositionInMillis = 0
        musicTimeInMillis = 0
        updateList()
    }
    private fun createMusic(context: Context, songMusic: SongMusic){
        if (mp !== null) return
        mp = MediaPlayer.create(context, songMusic.uri)
        musicTimeInMillis = (mp!!.duration).toLong()
    }

    fun startSound(context: Context, songMusic: SongMusic) {
        createMusic(context, songMusic)
        if(songMusic.isPlay)
            mp?.start()
    }

    fun onPlaySound(context: Context, songMusic: SongMusic){
        createMusic(context, songMusic)
        mp?.start()
        songMusic.isPlay = true
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

    private fun getDir(): List<Dir> = runBlocking(Dispatchers.IO) {
        var list = listOf<Dir>()
        Repositories.dirRepository.getDirList(true)
            .collect{
                list = it
            }
        return@runBlocking list
    }

    private var skipSongs = listOf<Song>()

    private fun updateListSkip() = runBlocking(Dispatchers.IO) {
        var list = listOf<MetaDataSong>()
        val listActiveSongs = mutableListOf<Song>()
        Repositories.metaSongsRepository.getSongs(true)
            .collect{
                list = it
            }
        list.forEach { listActiveSongs.add(Song(Uri.parse(it.uri))) }
        Log.d("Diiir", list.size.toString()+ " l")
        skipSongs = listActiveSongs.toList()
        Log.d("Diiir", skipSongs.size.toString()+ " s")
    }

    fun updateList(){
        updateListSkip()
        val uris = mutableListOf<Uri>()

        val listOFMusic = mutableListOf<File>()
        val listFile = mutableListOf<File>()
        val list = getDir()

        for (l in list){
            listFile.add(File(l.uri))
        }
        for(f in listFile){
            if (f.isDirectory && f.listFiles() != null) listOFMusic.addAll(f.listFiles()!!)
        }

        for (u in listOFMusic) {
            if (equalsWithSupportedFormat(getFormatFile(u.name)))
                uris.add(Uri.fromFile(u))
        }
        if(songs == uris) return
        songs = uris
        for (uri in songs)
            musicList.add(SongMusic(uri))
    }


}

