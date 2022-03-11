package com.example.fullproject.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.Song
import java.lang.Exception
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import com.example.fullproject.R
import com.example.fullproject.model.PlaylistSound
import com.example.fullproject.tasks.millisToMinute

class MusicPlayerFragment2 : Fragment() {
    private lateinit var binding: FragmentMusicPlayerBinding
    private val song = Song(R.raw.crush)
    private val songs = mutableListOf(song)
    lateinit var playlist : PlaylistSound

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playlist = PlaylistSound(context,songs){updateUI()}
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        controlSound(playlist.playlist[0])
        return binding.root
    }

    private fun controlSound(song: Song) {

        binding.play.setOnClickListener {
           playlist.playSoundPlayer(song.id)
        }

        binding.pause.setOnClickListener {
            playlist.pauseSoundPlayer()
        }
        binding.stop.setOnClickListener {
            playlist.stopSoundPlayer()
        }

        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playlist.playSound(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                playlist.pauseSound()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                playlist.continueSound()
            }
        })
    }


    private fun updateUI() {
        binding.timeView.progress = playlist.currentPosition
        binding.timeNow.text = millisToMinute(playlist.currentPosition)
        binding.timeAll.text = millisToMinute(playlist.musicTime)
        binding.timeView.max = playlist.musicTime
        Log.d("MainActivity", "handler.postDelayed(object")
    }

    companion object {

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}