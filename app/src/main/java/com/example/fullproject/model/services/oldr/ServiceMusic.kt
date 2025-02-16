package com.example.fullproject.model.services.oldr

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.fullproject.DBRepositories
import com.example.fullproject.model.songpack.entities.Song
import com.example.fullproject.model.songpack.entities.SongMapper
import com.example.fullproject.model.dirpack.entities.Directory
import com.example.fullproject.model.room.song.controller.MusicController
import com.example.fullproject.model.songpack.entities.MetaDataSong
import com.example.fullproject.utils.equalsWithSupportedFormat
import com.example.fullproject.utils.getFormatFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class ServiceMusic : Service() {
    private lateinit var audioManager: AudioManager
    private lateinit var afListener: AFListener

    private var mp: MediaPlayer? = null
    private var skipSongs = listOf<Song>()


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
        DBRepositories.init(this@ServiceMusic.applicationContext)
        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        println("In flow123")
        GlobalScope.launch(Dispatchers.IO) { updateData() }


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

    private fun createMusic(song: Song){
        lastSong = if (mp == null) song else currentSong
        currentSong = song
        mp = MediaPlayer.create(application.applicationContext, song.uri)
        duration = (mp?.duration ?: 0).toLong()
    }

    private fun startSound(song: Song) {
        if (song != currentSong || mp == null) createMusic(song)
        if(isPlay)mp?.start()
    }

    fun onPlay(song: Song){
        startSound(song)
        mp?.start()
        isPlay = true
        Log.d("AudioStream", "($currentSong): play")
    }

    fun onStop(){
        if (mp == null) return
        mp?.stop()
        mp?.release()
        isPlay = false
        currentTime = 0L
        mp = null
        Log.d("AudioStream", "($currentSong): stop")
    }

    fun onSoundPause() {
        if (mp == null) return
        mp!!.pause()
        isPlay = false
        Log.d("AudioStream", "($currentSong): pause")
    }

    private fun pauseSound(){
        if (isPlay) mp!!.pause()
    }

    fun setTimeSound(progress: Long){
        currentTime = progress
        mp?.seekTo(progress.toInt())
        Log.d("AudioStream", "($currentSong): new time in millis$")
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

        val newMusicIndex = curId + moveBy
        val isP = isPlay
        onStop()
        isPlay = isP
        startSound(songs[newMusicIndex])
    }

    fun updateCurrentPosition() {
        currentTime = (mp?.currentPosition ?: 0).toLong()
    }

    suspend fun updateData(){
        updateListSkip()
        updateListSongs()
    }

    private suspend fun getDir(onlyActive: Boolean): List<Directory> = withContext(Dispatchers.IO) {
        var list = listOf<Directory>()
        DBRepositories.dirRepository.getDirList(onlyActive)
            .first(){
                list = it
                true
            }
        return@withContext list
    }

    private suspend fun updateListSkip() = withContext(Dispatchers.IO) {
        var list = listOf<MetaDataSong>()
        val listActiveSongs = mutableListOf<Song>()
        DBRepositories.metaSongsRepository.getSongs(true)
            .first(){
                list = it
                true
            }
        list.forEach { listActiveSongs.add(Song(Uri.parse(it.uri))) }
        skipSongs = listActiveSongs.toList()
    }

    private suspend fun updateListSongs() = withContext(Dispatchers.IO){
        val listOFMusic = mutableListOf<File>()
        val listFile = mutableListOf<File>()
        val uris = mutableListOf<Song>()
        val dirList = getDir(true)

        for (l in dirList) listFile.add(File(l.uri))
        for(f in listFile) if (f.isDirectory && f.listFiles() != null) listOFMusic.addAll(f.listFiles()!!)
        for (u in listOFMusic) {
            if (equalsWithSupportedFormat(getFormatFile(u.path.toString())))
                uris.add(SongMapper.Base(Uri.fromFile(u)).map())
        }

        if (uris == songs) return@withContext
        songs = uris
        if (currentSong !in songs) {
            onSoundPause()
        }
    }
}