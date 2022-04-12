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
import com.example.fullproject.databinding.AppBarMainBinding
import com.example.fullproject.model.ControlSound
import com.example.fullproject.tasks.listFilesWithSubFolders
import com.example.fullproject.tasks.listFilesWithSubFolders2
import com.example.fullproject.tasks.millisToMinute

class MusicPlayerFragment : Fragment() {
    private lateinit var binding: FragmentMusicPlayerBinding
    private lateinit var controlSound : ControlSound
    private val song = Song(R.raw.crush)
    private val songs = mutableListOf(song)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controlSound = ControlSound(context,songs){updateUI()}
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        controlSound(controlSound.playlist[0])
        return binding.root
    }

    private fun controlSound(song: Song) {
        binding.playOrPause.setOnClickListener {
            if(!controlSound.isPlay) {
                controlSound.playSoundPlayer(song.id)
            }
            else {
                controlSound.pauseSoundPlayer()
            }
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
        val files = listFilesWithSubFolders2()
        for(f in files!!){
            binding.nameMusicHeader.text = f.isFile.toString()
        }
        binding.timeView.progress = controlSound.currentPositionInMillis

        if(binding.currentTime.text != millisToMinute(controlSound.currentPositionInMillis))
        binding.currentTime.text = millisToMinute(controlSound.currentPositionInMillis)

        if(binding.timeAll.text != millisToMinute(controlSound.musicTimeInMillis))
        binding.timeAll.text = millisToMinute(controlSound.musicTimeInMillis)

        if(binding.timeView.max != controlSound.musicTimeInMillis)
        binding.timeView.max = controlSound.musicTimeInMillis

        if(!controlSound.isPlay)
            binding.playOrPause.setImageResource(R.drawable.ic_play)
        else
            binding.playOrPause.setImageResource(R.drawable.ic_pause)
    }


    companion object {

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}