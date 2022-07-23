package com.example.fullproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fullproject.R
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.tasks.millisToMinute

class MusicPlayerFragment2() : Fragment() {

    private lateinit var binding: FragmentMusicPlayerBinding

    private val viewModel: MusicPlayerViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentTime = requireArguments().getLong(CURRENT_TIME)
        viewModel.song = requireArguments().getParcelable(CURRENT_SONG)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container,false)

        return binding.root
    }

    private fun controlSound(){
        binding.playOrPause.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun updateUI(){
//        binding.timeView.progress = soundService.currentPositionInMillis.toInt()
//
//        if(binding.currentTime.text != millisToMinute(soundService.currentPositionInMillis.toInt()))
//            binding.currentTime.text = millisToMinute(soundService.currentPositionInMillis.toInt())
//
//        if(binding.timeAll.text != millisToMinute(soundService.musicTimeInMillis.toInt()))
//            binding.timeAll.text = millisToMinute(soundService.musicTimeInMillis.toInt())
//
//        if(binding.timeView.max != soundService.musicTimeInMillis.toInt())
//            binding.timeView.max = soundService.musicTimeInMillis.toInt()
//
//
//        if(soundService.isPlay) {
//            binding.playOrPause.setImageResource(R.drawable.ic_pause)
//        } else {
//            binding.playOrPause.setImageResource(R.drawable.ic_play)
//        }
    }

    companion object{

        @JvmStatic
        val CURRENT_TIME = "Current time"

        @JvmStatic
        val CURRENT_SONG = "Current song"

        @JvmStatic
        fun newInstance(time: Long, song : SongMusic): MusicPlayerFragment2{
            val fragment = MusicPlayerFragment2()
            fragment.arguments = bundleOf(
                CURRENT_TIME to time,
                CURRENT_SONG to song
            )
            return fragment
        }
    }
}
