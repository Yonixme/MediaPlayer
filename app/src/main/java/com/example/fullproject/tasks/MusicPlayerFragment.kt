package com.example.fullproject.tasks

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

class MusicPlayerFragment : Fragment() {
    lateinit var binding: FragmentMusicPlayerBinding
    private var mp: MediaPlayer? = null
    private var isPlay : Boolean = true
    val song = Song(R.raw.crush)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        controlSound(song.currentSong)
        return binding.root
    }

    private fun controlSound(id:Int){
        binding.play.setOnClickListener{
            if(mp == null){
                mp = MediaPlayer.create(context,id)
                Log.d("MainActivity", "ID: ${mp!!.audioSessionId}")
                initialiseSeekBar()
            }
            mp?.start()
            binding.timeAll.text = nowTime(mp!!.duration)
            if(!isPlay) isPlay = !isPlay
        }

        binding.pause.setOnClickListener {
            if (mp !== null) {
                mp?.pause()
            }
            if(isPlay) isPlay = !isPlay
        }
        binding.stop.setOnClickListener{
            if(mp !== null){
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
                updateUI(0)
            }
        }
        binding.timeView.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    val currentPosition = mp?.currentPosition ?: 0
                    updateUI(currentPosition)
                    mp?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if(isPlay) mp?.pause()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(isPlay) mp?.start()
            }
        })
    }

    private fun initialiseSeekBar(){
        binding.timeView.max = mp!!.duration

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object: Runnable{
            override fun run() {
                try {
                    val currentPosition = mp?.currentPosition ?: 0
                    updateUI(currentPosition)
                    handler.postDelayed(this, 1000)
                }catch (e: Exception){
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

    companion object{

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}