package com.example.fullproject.screens.musicplayer.newr

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fullproject.R
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.room.song.entities.SongWithDetails
import com.example.fullproject.screens.musicplayer.newr.MusicPlayerViewModel.CustomSongState
import com.example.fullproject.utils.millisToMinute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicPlayerFragment : Fragment(R.layout.fragment_music_player) {
    private lateinit var binding: FragmentMusicPlayerBinding
    private val viewModel: MusicPlayerViewModel by viewModels()
    private var newSongSelected: Boolean = false
    private var observeOnUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = requireArguments().getString(ARG_URI)
        if (uri == null) findNavController().popBackStack()
        else viewModel.initSelectedSong(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMusicPlayerBinding.bind(view)

        binding.onDbScreen.setOnClickListener{ openScreenListsDb() }
        binding.backBtn.setOnClickListener{ onBack() }
        binding.playOrPause.setOnClickListener{ onPlayOrPause() }
        binding.stop.setOnClickListener{ onStopMusic()}
        binding.next.setOnClickListener{
            viewModel.nextSong()
            newSongSelected = true
        }
        binding.previous.setOnClickListener{
            viewModel.previousSong()
            newSongSelected = true
        }

        timeViewUI()
        updateUI()
    }

    private fun onBack(){
        findNavController().popBackStack()
    }

    private fun onPlayOrPause(){
        val currentSongState = viewModel.currentSongState.value ?: return

        when(currentSongState){
            is CustomSongState.Loading -> { viewModel.onPlay() }
            is CustomSongState.Success -> {
                if(currentSongState.currentSong?.song?.uri != viewModel.selectedSong.value?.song?.uri){
                    viewModel.onPlay()
                    return
                }
                if (currentSongState.currentSong?.isPlaying == null) return
                println("debug123 ${currentSongState.currentSong.isPlaying}")
                if (currentSongState.currentSong.isPlaying){
                    viewModel.onPause()
                }else{
                    viewModel.onPlay()
                }
            }
            is CustomSongState.Empty -> {  }
            is CustomSongState.Error -> { }
        }
    }

    private fun onStopMusic(){
        viewModel.onStop()
    }

    private fun timeViewUI(){
        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setCurrentTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.stopTimer()
                viewModel.pauseMusic()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.startUpdatingTimer()
                viewModel.continueMusic()
            }
        }
        )
    }

    private fun updateUI(){
        viewModel.currentSongState.observe(viewLifecycleOwner) { songUpdatingState ->
            when (songUpdatingState) {
                is CustomSongState.Loading -> { drawUI() }
                is CustomSongState.Success -> {
                    if (songUpdatingState.currentSong?.song?.uri == viewModel.selectedSong.value?.song?.uri) {
                        observeOnUpdating = true
                        if (!newSongSelected && songUpdatingState.currentSong?.song?.uri != null)
                            viewModel.initSelectedSong(songUpdatingState.currentSong.song.uri)
                    }
                    if (newSongSelected && songUpdatingState.currentSong?.song?.uri != null) {
                        requireArguments().putString(ARG_URI, songUpdatingState.currentSong.song.uri)
                        viewModel.initSelectedSong(songUpdatingState.currentSong.song.uri)
                        newSongSelected = false
                    }
                    if (viewModel.selectedSong.value?.song?.uri != songUpdatingState.currentSong?.song?.uri && !observeOnUpdating) {
                        drawUI()
                    } else {
                        drawUI(valueForDrawing = songUpdatingState.currentSong )
                    }
                }
                is CustomSongState.Empty -> { drawUI() }
                is CustomSongState.Error -> { }
            }
        }

        viewModel.selectedSong.observe(viewLifecycleOwner){songWithDetails->
            if (songWithDetails == null) findNavController().popBackStack()
        }
    }

    private fun drawUI(valueForDrawing: SongWithDetails? = viewModel.selectedSong.value){
        val defaultValue = valueForDrawing ?: return
        println("Debug in fragment111 $defaultValue")
        if (defaultValue.isPlaying) {
            binding.playOrPause.setImageResource(R.drawable.ic_pause)
            viewModel.startUpdatingTimer()
        }
        else
            binding.playOrPause.setImageResource(R.drawable.ic_play)

        binding.timeAll.text = millisToMinute(defaultValue.duration)
        binding.currentTime.text = millisToMinute(defaultValue.currentPosition)
        binding.nameMusicHeader.text = defaultValue.song.name ?: requireContext().getString(R.string.unnamed)
        binding.nameMusicPlaying.text = defaultValue.song.uri
        binding.timeView.max = defaultValue.duration
        binding.timeView.progress = defaultValue.currentPosition
    }

    private fun openScreenListsDb(){
        findNavController().navigate(R.id.action_musicPlayerFragment_to_dataBaseListFragment)
    }

    companion object{
        const val ARG_URI = "uri"
    }
}