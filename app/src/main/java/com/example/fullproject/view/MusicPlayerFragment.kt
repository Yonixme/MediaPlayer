package com.example.fullproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fullproject.NavigatorPlaylist
import com.example.fullproject.R
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.navigator
import com.example.fullproject.businesslogic.millisToMinute

class MusicPlayerFragment : Fragment(), NavigatorPlaylist{

    private lateinit var binding: FragmentMusicPlayerBinding

    private val viewModel: MusicPlayerViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentPosition = requireArguments().getLong(CURRENT_TIME)
        viewModel.song = requireArguments().getParcelable(CURRENT_SONG)!!
        viewModel.navigator = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container,false)
        controlSound()
        return binding.root
    }

    private fun controlSound(){
        viewModel.startSound(requireContext())
        binding.playOrPause.setOnClickListener {
            if (viewModel.song.isPlay){
                viewModel.onSoundPause()
            }else{
                viewModel.onSoundPlay(requireContext())
            }
        }
        binding.stop.setOnClickListener {
            viewModel.onSoundStop()
        }

        binding.back1.setOnClickListener{
            navigator().goBack()
        }

        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setTimeSound(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.pauseTimeSound()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.continueTimeSound()
            }
        })

        binding.next.setOnClickListener{
            viewModel.nextSound(requireContext())
        }

        binding.previous.setOnClickListener{
            viewModel.previouslySound(requireContext())
        }
    }

    override fun updateUI(){
        binding.timeView.progress = viewModel.currentPosition.toInt()

        if(binding.currentTime.text != millisToMinute(viewModel.currentPosition.toInt()))
            binding.currentTime.text = millisToMinute(viewModel.currentPosition.toInt())

        if(binding.timeAll.text != millisToMinute(viewModel.getDuration().toInt()))
            binding.timeAll.text = millisToMinute(viewModel.getDuration().toInt())

        if(binding.timeView.max != viewModel.getDuration().toInt())
            binding.timeView.max = viewModel.getDuration().toInt()


        if(viewModel.song.isPlay) {
            binding.playOrPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playOrPause.setImageResource(R.drawable.ic_play)
        }

        if(binding.nameMusicHeader.text != viewModel.song.uri.lastPathSegment)
            binding.nameMusicHeader.text = viewModel.song.uri.lastPathSegment

        if(binding.nameMusicPlaying.text != viewModel.song.uri.lastPathSegment)
            binding.nameMusicPlaying.text = viewModel.song.uri.lastPathSegment
    }


    companion object{

        @JvmStatic
        val CURRENT_TIME = "Current time"

        @JvmStatic
        val CURRENT_SONG = "Current song"

        @JvmStatic
        fun newInstance(time: Long, song : SongMusic): MusicPlayerFragment{
            val fragment = MusicPlayerFragment()
            fragment.arguments = bundleOf(
                CURRENT_TIME to time,
                CURRENT_SONG to song
            )
            return fragment
        }
    }
}