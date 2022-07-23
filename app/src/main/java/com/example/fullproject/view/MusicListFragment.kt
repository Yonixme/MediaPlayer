package com.example.fullproject.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.App
import com.example.fullproject.R
import com.example.fullproject.SongActionListener
import com.example.fullproject.SongAdapter
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.FragmentMusicListBinding

class MusicListFragment : Fragment() {
    private val requestSinglePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::updateList
    )
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var adapter: SongAdapter
    private val soundServiceMusic = App().soundServiceMusic
    private val viewModel: MusicListViewModel by viewModels { factory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicListBinding.inflate(inflater, container, false)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            binding.requestPermission.visibility = View.VISIBLE
            binding.requestPermission.setOnClickListener{
                requestSinglePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        adapter = SongAdapter(object: SongActionListener{

            override fun onStartSound(song: SongMusic) {
                Log.d("ssssss", "play")
                viewModel.onSoundPlay(requireContext(), song)
            }

            override fun onPauseSound(song: SongMusic) {
                Log.d("ssssss", "pause")
                viewModel.onSoundPause(song)
            }

            override fun onStopSound(song: SongMusic) {
                Log.d("ssssss", "stop")
                viewModel.onSoundStop(song)
            }

            override fun onMusicPlaylist(song: SongMusic) {
                Log.d("${song.uri.lastPathSegment}", "massage")
                viewModel.onMusicPlayer(song)
                runWhenActive {
                    parentFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, MusicPlayerFragment2.newInstance(viewModel.getCurrentTime(), song))
                    .commit()
                }
            }

            override fun onSetName() {
                Log.d("Menu" , "set name")
            }
        })
        updateUI()
        return binding.root
    }



    private fun updateList(granted: Boolean){
        if (granted)
        {
            binding.requestPermission.visibility = View.GONE
            updateUI()
        }
        else
        {
            if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                binding.requestPermission.visibility = View.VISIBLE
            else
                binding.requestPermission.visibility = View.VISIBLE
        }
    }

    fun runWhenActive(task: () -> Unit){
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            task()
        }
    }

    private fun updateUI(){
        soundServiceMusic.updateList()
        for (u in soundServiceMusic.songs)
            adapter.listSong.add(SongMusic(u))

        val layoutManager = LinearLayoutManager(context)
        binding.ListMusic.layoutManager = layoutManager
        binding.ListMusic.adapter = adapter
    }
}