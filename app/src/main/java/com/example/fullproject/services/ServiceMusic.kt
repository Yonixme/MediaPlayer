package com.example.fullproject.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.fullproject.businesslogic.equalsWithSupportedFormat
import com.example.fullproject.businesslogic.getFormatFile
import com.example.fullproject.services.model.songpack.entities.Song
import com.example.fullproject.services.model.SongMapper
import java.io.File

class ServiceMusic : Service() {
    private lateinit var audioManager: AudioManager
    private var mp: MediaPlayer? = null
    private lateinit var afListener: AFListener

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
    var songs = mutableListOf<Song>()
        private set

    private val binder = MyServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class MyServiceBinder : Binder() {
        fun myService(): ServiceMusic{
            return this@ServiceMusic
        }
    }

    override fun onCreate() {
        super.onCreate()
        updateData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun createMusic(song: Song){
        lastSong = if (mp == null) song else currentSong
        currentSong = song
        mp = MediaPlayer.create(application.applicationContext, song.uri)
        duration = (mp!!.duration).toLong()
    }

    private fun startSound(song: Song) {
        if (currentSong != song) createMusic(song)
        if(isPlay) mp?.start()
    }

    private fun checkAudioFocusForStartSound(song: Song)= run {
        when(getAFocus()){
            AudioManager.AUDIOFOCUS_GAIN -> startSound(song)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAFocus(): Int{
        afListener = AFListener(currentSong, "sss")
        return audioManager.requestAudioFocus(afListener,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    inner class AFListener(private val song: Song, private val str: String) :
        AudioManager.OnAudioFocusChangeListener {

        override fun onAudioFocusChange(focusChange: Int) {
            val logText: String = when(focusChange){
                AudioManager.AUDIOFOCUS_LOSS -> {
                    pauseSound()
                    "AUDIO_FOCUS_LOSS"
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    pauseSound()
                    "AUDIO_FOCUS_LOSS_TRANSIENT"
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    pauseSound()
                    "AUDIO_FOCUS_LOSS_TRANSIENT_CAN_DUCK"
                }

                AudioManager.AUDIOFOCUS_GAIN -> {
                    startSound(song)
                    "AUDIO_FOCUS_GAIN"
                }

                else -> "else"

            }
            Log.d("Audio_Focus", song.toString() + logText + str)
        }
    }

    fun onPlay(song: Song){
        checkAudioFocusForStartSound(song)
        mp?.start()
        isPlay = true
    }

    fun onStop(){
        mp?.stop()
        mp?.reset()
        mp?.release()
        currentTime = 0L
    }

    @Suppress("DEPRECATION")
    fun onSoundPause() {
        if (mp == null) return
        pauseSound()
        audioManager.abandonAudioFocus { afListener }
    }

    private fun pauseSound(){
        mp?.pause()
        isPlay = false
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