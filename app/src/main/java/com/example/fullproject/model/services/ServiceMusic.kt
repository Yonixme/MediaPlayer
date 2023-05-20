package com.example.fullproject.model.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.fullproject.Repositories
import com.example.fullproject.model.Song
import com.example.fullproject.model.songpack.entities.SongMapper
import com.example.fullproject.model.sqlite.dirpack.entities.Dir
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong
import com.example.fullproject.utils.equalsWithSupportedFormat
import com.example.fullproject.utils.getFormatFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File


class ServiceMusic() : Service() {
    private lateinit var audioManager: AudioManager
    private var mp: MediaPlayer? = null
    private var skipSongs = listOf<Song>()
    private lateinit var afListener: AFListener

    var songs: MutableList<Song> = mutableListOf()
        private set
    var currentSong: Song = SongMapper.Base(Uri.parse(" ")).map()
        private set
    var lastSong: Song = SongMapper.Base(Uri.parse(" ")).map()
        private set
    var isPlay = false
        private set
    var duration = 0L
        private set
    var currentTime = 0L
        private set
    var isAudioFocusLose = false
        private set

    private val binder = MyServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class MyServiceBinder() : Binder() {
        fun myService(): ServiceMusic {
            return this@ServiceMusic
        }
    }

    inner class AFListener(private val song: Song, private val str: String) :
        AudioManager.OnAudioFocusChangeListener {

        override fun onAudioFocusChange(focusChange: Int) {
            val logText: String = when(focusChange){
                AudioManager.AUDIOFOCUS_LOSS -> {
                    pauseSound()
                    isAudioFocusLose = true
                    "AUDIO_FOCUS_LOSS"
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    pauseSound()
                    isAudioFocusLose = true
                    "AUDIO_FOCUS_LOSS_TRANSIENT"
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    pauseSound()
                    isAudioFocusLose = true
                    "AUDIO_FOCUS_LOSS_TRANSIENT_CAN_DUCK"
                }

                AudioManager.AUDIOFOCUS_GAIN -> {
                    startSound(currentSong)
                    isAudioFocusLose = false
                    "AUDIO_FOCUS_GAIN"
                }

                else -> "else"

            }
            Log.d("Audio_Focus", song.toString() + logText + str)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Repositories.init(this@ServiceMusic.applicationContext)
        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        updateData()
        afListener = AFListener(currentSong, "sss")
        audioManager.requestAudioFocus(afListener,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.abandonAudioFocus { afListener }
    }

//    @Suppress("DEPRECATION")
//    private fun getAuFocus(): Int{
//        return audioManager.requestAudioFocus(afListener,
//            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
//    }
//
//    private fun checkAudioFocusStartSound(song: Song)= run {
//        when(getAuFocus()){
//            AudioManager.AUDIOFOCUS_GAIN -> startSound(song)
//
//            AudioManager.AUDIOFOCUS_LOSS -> pauseSound()
//        }
//    }

    private fun createMusic(song: Song){
        lastSong = if (mp == null) song else currentSong
        currentSong = song
        mp = MediaPlayer.create(application.applicationContext, song.uri)
        duration = (mp!!.duration).toLong()
    }

    private fun startSound(song: Song) {
        if (song != currentSong || mp == null) createMusic(song)
        if(isPlay)mp?.start()
    }

    fun onPlay(song: Song){
        startSound(song)
        mp?.start()
        isPlay = true
    }

    fun onStop(){
        if (mp == null) return
        mp?.stop()
        mp?.release()
        isPlay = false
        currentTime = 0L
        mp = null
    }

    fun onSoundPause() {
        if (mp == null) return
        mp!!.pause()
        isPlay = false
    }

    private fun pauseSound(){
        if (isPlay) mp!!.pause()
    }

    fun setTimeSound(progress: Long){
        currentTime = progress
        mp?.seekTo(progress.toInt())
    }

    fun pauseTimeSound(){
        if (isPlay) {
            mp?.pause()
        }
    }

    fun continueTimeSound(){
        if (isPlay) {
            mp?.start()
        }
    }

    fun nextSound(){
        changeCurrentSong(1)
    }

    fun previousSound(){
        changeCurrentSong(-1)
    }

    private fun changeCurrentSong(moveBy: Int){
        if(currentSong.uri.toString().isBlank() && songs.size > 0) currentSong = songs[0]
        val curId = songs.indexOf(currentSong)
        if ((curId + moveBy == songs.size) or (curId + moveBy < 0)) return
        Log.d("ddddddd", "app.getMusicService().isPlay.toString()dddddddddddddd")
        val newMusicIndex = curId + moveBy
        val isP = isPlay
        onStop()
        isPlay = isP
        startSound(songs[newMusicIndex])
    }

    fun updateCurrentPosition() {
        currentTime = (mp?.currentPosition ?: 0).toLong()
    }

    fun updateData(){
        updateListSkip()
        updateListSongs()
    }

    private fun getDir(onlyActive: Boolean): List<Dir> = runBlocking(Dispatchers.IO) {
        var list = listOf<Dir>()
        Repositories.dirRepository.getDirList(onlyActive)
            .collect{
                list = it
            }
        return@runBlocking list
    }

    private fun updateListSkip() = runBlocking(Dispatchers.IO) {
        var list = listOf<MetaDataSong>()
        val listActiveSongs = mutableListOf<Song>()
        Repositories.metaSongsRepository.getSongs(true)
            .collect{
                list = it
            }
        list.forEach { listActiveSongs.add(Song(Uri.parse(it.uri))) }
        skipSongs = listActiveSongs.toList()
    }

    private fun updateListSongs(){
        val listOFMusic = mutableListOf<File>()
        val listFile = mutableListOf<File>()
        val uris = mutableListOf<Song>()
        val list = getDir(true)

        for (l in list) listFile.add(File(l.uri))
        for(f in listFile) if (f.isDirectory && f.listFiles() != null) listOFMusic.addAll(f.listFiles()!!)
        for (u in listOFMusic) {
            if (equalsWithSupportedFormat(getFormatFile(u.name)))
                uris.add(SongMapper.Base(Uri.fromFile(u)).map())
        }

        if (uris == songs) return
        songs = uris
        if (currentSong !in songs) {
            onSoundPause()
        }
    }
}