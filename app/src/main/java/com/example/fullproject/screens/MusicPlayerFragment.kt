package com.example.fullproject.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.Song
import android.widget.SeekBar
import com.example.fullproject.R
import com.example.fullproject.model.PlaylistSound
import com.example.fullproject.tasks.millisToMinute

class MusicPlayerFragment : Fragment() {
    private lateinit var binding: FragmentMusicPlayerBinding
    private val song = Song(R.raw.crush)
    private val songs = mutableListOf(song)
    lateinit var playlist : PlaylistSound

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        playlist = PlaylistSound(context,songs){updateUI()}
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
                    playlist.setTimeSound(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                playlist.pauseTimeSound()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                playlist.continueTimeSound()
            }
        })
    }


    private fun updateUI() {
        binding.timeView.progress = playlist.currentPositionInMillis
        binding.timeNow.text = millisToMinute(playlist.currentPositionInMillis)
        binding.timeAll.text = millisToMinute(playlist.musicTimeInMillis)

        if(binding.timeView.max != playlist.musicTimeInMillis)
        binding.timeView.max = playlist.musicTimeInMillis
    }

    companion object {

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}