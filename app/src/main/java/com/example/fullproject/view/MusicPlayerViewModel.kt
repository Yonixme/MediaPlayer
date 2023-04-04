package com.example.fullproject.view

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.fullproject.App
import com.example.fullproject.PlayerManager
import com.example.fullproject.businesslogic.SongMusic

class MusicPlayerViewModel(private val app: App) : BaseMusicViewModel(app) {
    var currentPosition: Long = 0
    var song: SongMusic = SongMusic(Uri.parse("Empty"))
    lateinit var manager: PlayerManager

    // Create logic for any SDK version
    private val audioManager: AudioManager = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun focus()= run {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            result1()
        }else if(
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            result2()
        } else{
            TODO("#3 Add logic for Android 11 and higher")
        }
    }

    @Suppress("DEPRECATION")
    private fun result1(): Int{
        val afListener = AFListener(song, "sss")
        return audioManager.requestAudioFocus(afListener,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }


    private fun result2(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val afListener = AFListener(song, "sss")
        val handler = Handler(Looper.getMainLooper())
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run{
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_GAME)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(afListener, handler)
            build()
        }

        val focusLock = Any()

        var playbackDelayed = false
        var playbackNowAuthorized = false

// requesting audio focus and processing the response
        val res = audioManager.requestAudioFocus(focusRequest)
        synchronized(focusLock) {
            playbackNowAuthorized = when (res) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> false
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    onSoundPlay(song)
                    true
                }
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                    playbackDelayed = true
                    false
                }
                else -> false
            }
        }
    }

    class AFListener(private val song: SongMusic, private val str: String) : OnAudioFocusChangeListener{

        override fun onAudioFocusChange(focusChange: Int) {
            val logText: String = when(focusChange){
                AudioManager.AUDIOFOCUS_LOSS ->  "AUDIO_FOCUS_LOSS"

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> "AUDIO_FOCUS_LOSS_TRANSIENT"

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> "AUDIO_FOCUS_LOSS_TRANSIENT_CAN_DUCK"

                AudioManager.AUDIOFOCUS_GAIN -> "AUDIO_FOCUS_GAIN"

                else -> "else"

            }
            Log.d("Audio_Focus", song.toString() + logText + str)
        }
    }

    // Created logic for any SDK version

    private lateinit var countTimer : CountDownTimer
    private var isTimerRun: Boolean = false


    fun onSoundPlay() {
        super.onSoundPlay(song)
        updateData()
        startTimer()
    }

    fun startSound(){
        super.startSound(song)
        setTimeSound(getCurrentTime())
        startTimer()
        updateData()
        }

    fun onSoundPause() {
        super.onSoundPause(song)
        stopTimer()
        updateData()
    }

    fun onSoundStop() {
        super.onSoundStop(song)
        stopTimer()
        updateData()

    }



    fun setTimeSound(progress: Long) {
        if (uriNotCorrect(song.uri)) return
        app.soundServiceMusic.setTimeSound(progress)
        updateData()
    }

    fun pauseTimeSound() {
        super.pauseSound(song)
        stopTimer()
    }

    fun continueTimeSound() {
        super.continueSound(song)
        updateData()
        startTimer()
    }
    fun previouslySound(){
        song = super.changeCurrentSong(song, -1)
        updateData()
    }

    fun nextSound(){
        song = super.changeCurrentSong(song, 1)
        updateData()
    }

    private fun stopTimer(){
        if(!isTimerRun) return
        countTimer.cancel()
        isTimerRun = false
    }

    private fun startTimer(){
        if (isTimerRun) return
        countTimer = object : CountDownTimer(
            app.soundServiceMusic.musicTimeInMillis - app.soundServiceMusic.currentPositionInMillis,
            1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateData()
            }
            override fun onFinish() {
                onSoundStop()
                updateData()
            }
        }
        countTimer.start()
        isTimerRun = true
    }

    fun updateData(){
        currentPosition = getCurrentTime()
        manager.updateStateElement()
    }
}








