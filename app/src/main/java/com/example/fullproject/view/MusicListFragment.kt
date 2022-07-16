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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.App
import com.example.fullproject.SongActionListener
import com.example.fullproject.SongAdapter
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.FragmentMusicListBinding

class MusicListFragment : Fragment() {
    private val requestSinglePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::pushRecyclerView
    )
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var adapter: SongAdapter
    private val uris: List<Uri> = App().soundServiceMusic.songs
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

            override fun onStartSound(songMusic: SongMusic) {
                Log.d("ssssss", "play")
                viewModel.onSoundPlay(requireContext(), songMusic)
            }

            override fun onPauseSound(songMusic: SongMusic) {
                Log.d("ssssss", "pause")
                viewModel.onSoundPause(songMusic)
            }

            override fun onStopSound(songMusic: SongMusic) {
                Log.d("ssssss", "stop")
                viewModel.onSoundStop(songMusic)
            }

            override fun onMusicPlaylist(song: SongMusic) {
                Log.d("dddd", "Object ${song.uri.lastPathSegment}")
            }

            override fun onSetName() {
                Log.d("Menu" , "set name")
            }
        })
//        adapter.listMusic = uris.toMutableList()
        for (u in uris)
            adapter.listSong.add(SongMusic(u))

        val layoutManager = LinearLayoutManager(context)
        binding.ListMusic.layoutManager = layoutManager
        binding.ListMusic.adapter = adapter
        return binding.root
    }

    private fun pushRecyclerView(granted: Boolean){
        if (granted)
        {
            binding.requestPermission.visibility = View.GONE
        }
        else
        {
            if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                binding.requestPermission.visibility = View.VISIBLE
            else
                binding.requestPermission.visibility = View.VISIBLE
        }
    }
}