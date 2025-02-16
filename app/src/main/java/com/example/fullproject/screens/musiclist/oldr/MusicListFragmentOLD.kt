package com.example.fullproject.screens.musiclist.oldr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import com.example.fullproject.model.songpack.entities.MetaDataSong
import com.example.fullproject.model.songpack.entities.Song
import com.example.fullproject.model.songpack.entities.SongPackage
import com.example.fullproject.utils.activityNavigator
import com.example.fullproject.utils.factory

class MusicListFragmentOLD : Fragment() {
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var adapter: SongAdapter
    private val viewModel: MusicListViewModelOLD by viewModels { factory() }

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

        checkNeededPermission()

        binding.openScreenListsDb.setOnClickListener{
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
        if (granted) {
            binding.requestPermission.visibility = View.GONE
            updateUI()
        } else {
            if((!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                Build.VERSION.SDK_INT < 33) || !shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO))
                binding.requestPermission.visibility = View.GONE
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
        if (Build.VERSION.SDK_INT >= 33) {
            if(ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                binding.requestPermission.visibility = View.VISIBLE
                binding.requestPermission.setOnClickListener {
                    requestSinglePermission.launch(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }else{
                binding.requestPermission.visibility = View.GONE
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                binding.requestPermission.visibility = View.VISIBLE
                binding.requestPermission.setOnClickListener {
                    requestSinglePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                binding.requestPermission.visibility = View.GONE
            }
        }
    }
}