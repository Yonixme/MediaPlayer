package com.example.fullproject.screens.musiclist

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.databinding.FragmentMusicListBinding
import com.example.fullproject.screens.viewmodel.MusicListViewModel
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong
import com.example.fullproject.model.Song
import com.example.fullproject.model.SongPackage
import com.example.fullproject.screens.musiclist.adapters.SongActionListener
import com.example.fullproject.screens.musiclist.adapters.SongAdapter
import com.example.fullproject.utils.activityNavigator
import com.example.fullproject.utils.factory

class MusicListFragment : Fragment() {
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var adapter: SongAdapter
    private val viewModel: MusicListViewModel by viewModels { factory() }

    private val requestSinglePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::updateList
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        binding.requestPermission.setOnClickListener { ActivityCompat.requestPermissions(activity as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED) }

        checkNeededPermission()

        binding.btnChangeDataInDb.setOnClickListener{
            activityNavigator().onDataBaseList()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter = SongAdapter(object: SongActionListener {
            override fun onStartSound(song: Song) {
                viewModel.onSoundPlay(song)
            }

            override fun onPauseSound() {
                viewModel.onSoundPause()
            }

            override fun onStopSound() {
                viewModel.onSoundStop()
            }

            override fun openMusicPlayer(song: Song) {
                val pushedSong = SongPackage(song.uri)
                runWhenActive { activityNavigator().onMusicPlaylist(pushedSong) }
            }

            override fun onSetName() {
                viewModel.notifyUserWhatElementWasTouched()
            }

            override fun getSongListWithDB(): List<MetaDataSong> {
                return viewModel.getSongsListWithDB()
            }

            override fun isPlaySound(): Boolean {
                return viewModel.isPlaySound()
            }

            override fun getCurrentSong(): Song {
                return viewModel.getCurrentSong()
            }
        })
        updateUI()
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
        val list = viewModel.getListSong()
        adapter.listSong = list
        val layoutManager = LinearLayoutManager(context)
        binding.ListMusic.layoutManager = layoutManager
        binding.ListMusic.adapter = adapter

        val sizes = if(list.size == 1) "${list.size} song" else "${list.size} songs"
        binding.PrintCountSongs.text = "Your music list to have $sizes"
    }

    private fun checkNeededPermission(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            binding.requestPermission.visibility = View.VISIBLE
            binding.requestPermission.setOnClickListener{
                requestSinglePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}