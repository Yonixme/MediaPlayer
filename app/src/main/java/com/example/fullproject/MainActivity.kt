package com.example.fullproject

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import com.example.fullproject.databinding.ActivityMainBinding
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    lateinit var binding :ActivityMainBinding
    private var mp: MediaPlayer? = null
    private var currentSong1 = mutableListOf(R.raw.crush)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controlSound(currentSong1[0]);
    }

    private fun controlSound(id:Int){
        binding.play.setOnClickListener{
            if(mp == null){
                mp = MediaPlayer.create(this,id)
                Log.d("MainActivity", "ID: ${mp!!.audioSessionId}")
                initialiseSeekBar()
            }
            mp?.start()
//            binding.timeNow.setText(Now(mp!!.currentPosition))
            //Log.d("MainActivity" , "Duration: ${mp!!.duration/1000} second")
            binding.timeAll.setText(Now(mp!!.duration))
        }

        binding.pause.setOnClickListener {
            if (mp !== null) {
                mp?.pause()
            }
        }
        binding.stop.setOnClickListener{
            if(mp !== null){
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
                binding.timeNow.setText(Now(0))
            }
        }
        binding.timeView.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
             if(fromUser){
                 mp?.seekTo(progress)
             }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun initialiseSeekBar(){

        binding.timeView.max = mp!!.duration

        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                try{
                    binding.timeView.progress = mp!!.currentPosition
                    binding.timeNow.setText(Now(mp!!.currentPosition))
                    handler.postDelayed(this,1000)

                }catch (e: Exception) {
                    binding.timeView.progress = 0
                }
            }
        }, 0)
    }


    private fun Now(progress: Int):String{
        var help:Int = progress/1000
        var minute: Int = 0
        var str:String = ""
        if(help > 59){
            minute = help/60
            help %= 60
        }
        str = "${minute.toString()}:${help.toString()}"
        return str
    }
}