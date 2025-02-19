package com.example.fullproject.screens.musiclist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.R
import com.example.fullproject.databinding.FragmentMusicListBinding
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.screens.musiclist.MusicListViewModel.*
import com.example.fullproject.screens.musicplayer.MusicPlayerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicListFragment: Fragment(R.layout.fragment_music_list) {

    private lateinit var binding: FragmentMusicListBinding
    private lateinit var adapter: SongAdapterNew
    private val viewModel: MusicListViewModel by viewModels()

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val notificationsGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        val mediaGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: false
        val storageGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

        if (notificationsGranted && (mediaGranted || storageGranted)) {
            binding.requestPermission.visibility = View.GONE
        } else {
            binding.requestPermission.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMusicListBinding.bind(view)

        adapter = SongAdapterNew(object : SongActionListenerNew{
            override fun onPlay(uri: String) {
                viewModel.onPlay(uri)
            }

            override fun onPause(uri: String) {
                viewModel.onPause(uri)
            }

            override fun onStop(uri: String) {
                viewModel.onStop(uri)
            }

            override fun openScreenWithDetails(uri: String) {
                openPlayListScreen(uri)
            }
        })
        val layoutManager = LinearLayoutManager(context)
        binding.ListMusic.layoutManager = layoutManager
        binding.ListMusic.adapter = adapter

        val  itemAnimator = binding.ListMusic.itemAnimator
        if(itemAnimator is DefaultItemAnimator){
            itemAnimator.supportsChangeAnimations = false
        }


        checkNeededPermission()

        binding.openScreenListsDb.setOnClickListener{
            openScreenListsDb()
        }

        viewModel.listSongWithDetails.observe(viewLifecycleOwner) { listSongWithDetails ->

            when (listSongWithDetails) {
                is ScreenStateWithDetails.Loading -> { screenInLoadingState()}
                is ScreenStateWithDetails.Success -> { updateListSongOnScreen(listSongWithDetails.listSong)}
                is ScreenStateWithDetails.Empty -> { updateUI(0) }
                is ScreenStateWithDetails.Error -> { }
            }
        }
    }


    private fun updateListSongOnScreen(listSongWithDetails: List<SongWithDetails>){
        adapter.listSongWithDetails = listSongWithDetails
        updateUI(listSongWithDetails.size)
    }

    private fun screenInLoadingState(){
        binding.PrintCountSongs.text = getString(R.string.your_music_list_loading)
    }

    private fun showRequestPermissionButton(granted: Boolean){
        if (granted) {
            binding.requestPermission.visibility = View.GONE
        } else {
            if((!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        Build.VERSION.SDK_INT < 33) || !shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_MEDIA_AUDIO)) {
                binding.requestPermission.visibility = View.VISIBLE
            }
            else {
                binding.requestPermission.visibility = View.GONE
            }
        }
    }

    private fun checkNeededPermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            binding.requestPermission.visibility = View.VISIBLE
            binding.requestPermission.setOnClickListener {
                requestMultiplePermissions.launch(permissionsToRequest.toTypedArray())
            }
        } else {
            binding.requestPermission.visibility = View.GONE
        }
    }


    private fun updateUI(listSize: Int){
        val stringWithSizeOfListMusic =
            when (listSize) {
                0 -> getString(R.string.is_empty)
                1 -> getString(R.string.to_have_song, listSize)
                else -> getString(R.string.to_have_songs, listSize)
            }

        binding.PrintCountSongs.text =
            getString(R.string.your_music_list, stringWithSizeOfListMusic)
    }

    private fun openScreenListsDb(){
        findNavController().navigate(R.id.action_musicListFragment_to_dataBaseListFragment)
    }

    private fun openPlayListScreen(uri: String){
        findNavController().navigate(
            R.id.action_musicListFragment_to_musicPlayerFragment,
            bundleOf(MusicPlayerFragment.ARG_URI to uri)
        )
    }
}