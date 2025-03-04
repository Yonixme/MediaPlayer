package com.example.fullproject.screens.musicplayer

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fullproject.R
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.screens.musicplayer.MusicPlayerViewModel.CustomSongState
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
                viewModel.pauseMusic()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.continueMusic()
            }
        }
        )
    }

    private fun updateUI(){
        viewModel.currentSongState.observe(viewLifecycleOwner) { songUpdatingState ->
            when (songUpdatingState) {
                is CustomSongState.Loading -> drawUI()
                is CustomSongState.Empty -> drawUI()
                is CustomSongState.Error -> return@observe

                is CustomSongState.Success -> {
                    val newUri = songUpdatingState.currentSong?.song?.uri
                    val selectedUri = viewModel.selectedSong.value?.song?.uri

                    observeOnUpdating = observeOnUpdating || (newUri == selectedUri)

                    if ((newSongSelected || observeOnUpdating) && newUri != null && newUri != selectedUri) {
                        requireArguments().putString(ARG_URI, newUri)
                        viewModel.initSelectedSong(newUri)
                    }

                    songUpdatingState.currentSong.takeIf { observeOnUpdating || newSongSelected}.let { songWithDetails->
                        if (songWithDetails != null){
                            drawUI(songWithDetails)
                        }else{
                            drawUI()
                        }
                    }
                }
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
        }
        else
            binding.playOrPause.setImageResource(R.drawable.ic_play)

        binding.timeAll.text = viewModel.millisToMinute(defaultValue.duration)
        binding.currentTime.text = viewModel.millisToMinute(defaultValue.currentPosition)
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