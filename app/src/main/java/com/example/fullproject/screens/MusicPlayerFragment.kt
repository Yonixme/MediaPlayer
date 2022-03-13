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
import com.example.fullproject.model.ControlSound
import com.example.fullproject.tasks.millisToMinute

class MusicPlayerFragment : Fragment() {
    private lateinit var binding: FragmentMusicPlayerBinding
    private lateinit var controlSound : ControlSound
    private val song = Song(R.raw.crush)
    private val songs = mutableListOf(song)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        controlSound = ControlSound(context,songs){updateUI()}
        controlSound(controlSound.playlist[0])
        return binding.root
    }

    private fun controlSound(song: Song) {
        binding.play.setOnClickListener {
           controlSound.playSoundPlayer(song.id)
        }

        binding.pause.setOnClickListener {
            controlSound.pauseSoundPlayer()
        }
        binding.stop.setOnClickListener {
            controlSound.stopSoundPlayer()
        }

        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    controlSound.setTimeSound(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                controlSound.pauseTimeSound()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                controlSound.continueTimeSound()
            }
        })
    }


    private fun updateUI() {
        binding.timeView.progress = controlSound.currentPositionInMillis

        if(binding.timeNow.text != millisToMinute(controlSound.currentPositionInMillis))
        binding.timeNow.text = millisToMinute(controlSound.currentPositionInMillis)

        if(binding.timeAll.text != millisToMinute(controlSound.musicTimeInMillis))
        binding.timeAll.text = millisToMinute(controlSound.musicTimeInMillis)

        if(binding.timeView.max != controlSound.musicTimeInMillis)
        binding.timeView.max = controlSound.musicTimeInMillis
    }

    companion object {

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}