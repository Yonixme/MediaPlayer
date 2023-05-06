package com.example.fullproject.screens.musiclist

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.*
import com.example.fullproject.services.model.SongMusic
import com.example.fullproject.databinding.FragmentMusicListBinding
import com.example.fullproject.screens.BaseListViewModel
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
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
            override fun onStartSound(song: SongMusic) {
                viewModel.onSoundPlay(song)
                viewModel.onError()
            }

            override fun onPauseSound(song: SongMusic) {
                viewModel.onSoundPause(song)
                viewModel.onError(context!!.resources.getString(R.string.music_not_found_alert))
            }

            override fun onStopSound(song: SongMusic) {
                viewModel.onSoundStop(song)
            }

            override fun openMusicPlayer(song: SongMusic) {
                val pushedSong = SongMusic(song.uri, song.isPlay)
                viewModel.onMusicPlayer(song)
                runWhenActive { activityNavigator().onMusicPlaylist(viewModel.getCurrentTime(), pushedSong) }
            }

            override fun onSetName() {
                Toast.makeText(context, "This method will be added later", Toast.LENGTH_LONG).show()
            }

            override suspend fun getSong(): List<MetaDataSong> {
                return BaseListViewModel.Base().getListSongWithDB(false)
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
        for (u in list)
            adapter.listSong.add(SongMusic(u))

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