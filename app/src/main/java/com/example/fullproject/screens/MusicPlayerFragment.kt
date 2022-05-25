package com.example.fullproject.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.Song
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fullproject.MainActivity
import com.example.fullproject.R
import com.example.fullproject.model.SoundService
import com.example.fullproject.tasks.millisToMinute
import java.io.File

class MusicPlayerFragment : Fragment() {
    private val f = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::dot
    )

    private lateinit var binding: FragmentMusicPlayerBinding
    private lateinit var soundService : SoundService
    private val song = Song(R.raw.crush)
    private val songs = mutableListOf(song)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundService = SoundService(context,songs){updateUI()}
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        controlSound(soundService.playlist[0])
        return binding.root
    }

    private fun controlSound(song: Song) {
        binding.playOrPause.setOnClickListener {
            if (soundService.isPlay) {
                soundService.onSoundPause()
            } else {
                soundService.onSoundPlay(song.id)
            }
        }
        binding.stop.setOnClickListener {
            soundService.onSoundStop()
            f.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


                val file = File("/storage/emulated/0/Download")
                Log.d("  file", "${file.isFile}")
                for (f in file.listFiles())
                    Log.d("  files", "${f.name}")
                for (f in file.listFiles())
                    Log.d("  files", "${f.absolutePath}")
            }

        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    soundService.setTimeSound(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                soundService.pauseTimeSound()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                soundService.continueTimeSound()
            }
        })
    }


    private fun updateUI() {
        binding.timeView.progress = soundService.currentPositionInMillis.toInt()

        if(binding.currentTime.text != millisToMinute(soundService.currentPositionInMillis.toInt()))
        binding.currentTime.text = millisToMinute(soundService.currentPositionInMillis.toInt())

        if(binding.timeAll.text != millisToMinute(soundService.musicTimeInMillis.toInt()))
        binding.timeAll.text = millisToMinute(soundService.musicTimeInMillis.toInt())

        if(binding.timeView.max != soundService.musicTimeInMillis.toInt())
        binding.timeView.max = soundService.musicTimeInMillis.toInt()


        if(soundService.isPlay) {
            binding.playOrPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playOrPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun dot(granted: Boolean){
        if (granted)
        {
            Toast.makeText(context, "sssssss", Toast.LENGTH_LONG).show()
        }
        else
        {
            if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                Toast.makeText(context, "dddd", Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}