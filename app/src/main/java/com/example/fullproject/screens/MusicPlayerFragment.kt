package com.example.fullproject.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.Song
import java.lang.Exception
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import com.example.fullproject.R
import com.example.fullproject.model.PlaylistSound
import com.example.fullproject.tasks.MusicNavigator

class MusicPlayerFragment : Fragment() {
    private lateinit var binding: FragmentMusicPlayerBinding
//    val playList = PlayList(context)
    private val song = Song(R.raw.crush)
    private val songs = mutableListOf(song)
   // val playlist = PlaylistSound(context,songs)
    private var mp: MediaPlayer? = null
    private var isPlay: Boolean = true



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        controlSound(song)
        return binding.root
    }

    private fun controlSound(song: Song) {
        binding.play.setOnClickListener {
            playSound(song.id)
        }

        binding.pause.setOnClickListener {
            pauseSound()
        }
        binding.stop.setOnClickListener {
            stopSound()
        }

        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mp?.seekTo(progress)
                    val currentPosition = mp?.currentPosition ?: 0
                    updateUI(currentPosition)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (isPlay) mp?.pause()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isPlay) mp?.start()
            }
        })
    }


    private fun initialiseSeekBar() {
        binding.timeView.max = mp!!.duration

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    val currentPosition = mp?.currentPosition ?: 0
                    updateUI(currentPosition)
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    val currentPosition = 0
                    updateUI(currentPosition)
                }
            }
        }, 0)
    }

    private fun updateUI(currentPosition: Int) {
        binding.timeView.progress = currentPosition
        binding.timeNow.text = nowTime(currentPosition)
    }

    private fun nowTime(progress: Int): String {
        var seconds: Int = progress / 1000

        val minute: Int
        if (seconds > 59) {
            minute = seconds / 60
            seconds %= 60
        } else minute = 0

        return if (seconds > 9) "$minute:$seconds"
        else "$minute:0$seconds"
    }

     private fun playSound(id: Int) {
        if (mp == null) {
            mp = MediaPlayer.create(context, id)
            Log.d("MainActivity", "ID: ${mp!!.audioSessionId}")
            initialiseSeekBar()
        }
        mp?.start()
        binding.timeAll.text = nowTime(mp!!.duration)
        if (!isPlay) isPlay = !isPlay
    }

    private fun pauseSound() {
        if (mp !== null) {
            mp?.pause()
        }
        if (isPlay) isPlay = !isPlay
    }

    private fun stopSound() {
        if (mp !== null) {
            mp?.stop()
            mp?.reset()
            mp?.release()
            mp = null
            updateUI(0)
        }
    }

    companion object {

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}

