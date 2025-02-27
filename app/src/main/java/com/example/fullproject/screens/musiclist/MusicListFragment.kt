package com.example.fullproject.screens.musiclist

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
class MusicListFragment : Fragment(R.layout.fragment_music_list) {
    private lateinit var binding: FragmentMusicListBinding
    private val viewModel: MusicListViewModel by viewModels()
    private lateinit var adapter: SongAdapterNew

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        binding.requestPermission.visibility = if (allGranted) View.GONE else View.VISIBLE

        if (allGranted) {
            viewModel.loadSongs()
        } else {
            checkShouldShowRationale(permissions.keys.filterNot { permissions[it] == true }.toTypedArray())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMusicListBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        checkAndRequestPermissions()
    }

    private fun setupRecyclerView() {
        adapter = SongAdapterNew(object : SongActionListenerNew {
            override fun onPlay(uri: String) = viewModel.onPlay(uri)
            override fun onPause(uri: String) = viewModel.onPause(uri)
            override fun onStop(uri: String) = viewModel.onStop(uri)
            override fun openScreenWithDetails(uri: String) = openPlayListScreen(uri)
        })

        with(binding.ListMusic) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MusicListFragment.adapter
            (itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        }
    }

    private fun setupClickListeners() {
        binding.run {
            openScreenListsDb.setOnClickListener { openScreenListsDb() }
            requestPermission.setOnClickListener {
                requestMultiplePermissions.launch(buildPermissionsList())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.listSongWithDetails.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ScreenStateWithDetails.Loading -> showLoadingState()
                is ScreenStateWithDetails.Success -> updateSongList(state.listSong)
                is ScreenStateWithDetails.Empty -> updateUI(0)
                is ScreenStateWithDetails.Error -> Unit
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = buildPermissionsList()
        if (permissions.isEmpty()) {
            binding.requestPermission.visibility = View.GONE
            viewModel.loadSongs()
        } else {
            binding.requestPermission.visibility = View.VISIBLE
            checkShouldShowRationale(permissions)
        }
    }

    private fun buildPermissionsList(): Array<String> {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= 33) {
            addPermissionIfNeeded(permissions, Manifest.permission.POST_NOTIFICATIONS)
            addPermissionIfNeeded(permissions, Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            addPermissionIfNeeded(permissions, Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return permissions.toTypedArray()
    }

    private fun addPermissionIfNeeded(permissions: MutableList<String>, permission: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(permission)
        }
    }

    private fun checkShouldShowRationale(permissions: Array<String>) {
        permissions.forEach { permission ->
            when {
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> return@forEach

                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission) -> {
                    showRationaleDialog()
                    return
                }

                else -> showPermissionDeniedForeverDialog()
            }
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_needed_title)
            .setMessage(R.string.permission_needed_message)
            .setPositiveButton(R.string.continue_text) { _, _ ->
                requestMultiplePermissions.launch(buildPermissionsList())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPermissionDeniedForeverDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_denied_forever_title)
            .setMessage(R.string.permission_denied_forever_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun updateSongList(songs: List<SongWithDetails>) {
        adapter.listSongWithDetails = songs
        updateUI(songs.size)
    }

    private fun showLoadingState() {
        binding.PrintCountSongs.text = getString(R.string.your_music_list_loading)
    }

    private fun updateUI(listSize: Int) {
        val stringRes = when (listSize) {
            0 -> R.string.is_empty
            1 -> R.string.to_have_song
            else -> R.string.to_have_songs
        }

        binding.PrintCountSongs.text = getString(
            R.string.your_music_list,
            getString(stringRes, listSize)
        )
    }

    private fun openScreenListsDb() {
        findNavController().navigate(R.id.action_musicListFragment_to_dataBaseListFragment)
    }

    private fun openPlayListScreen(uri: String) {
        findNavController().navigate(
            R.id.action_musicListFragment_to_musicPlayerFragment,
            bundleOf(MusicPlayerFragment.ARG_URI to uri)
        )
    }
}