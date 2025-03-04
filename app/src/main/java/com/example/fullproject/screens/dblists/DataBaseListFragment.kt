package com.example.fullproject.screens.dblists

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.R
import com.example.fullproject.databinding.FragmentDatabaseListBinding
import com.example.fullproject.model.directory.entities.Directory
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.screens.components.AddingDirInDbDialog
import com.example.fullproject.screens.components.AddingSongInDbDialog
import com.example.fullproject.screens.dblists.DataBaseListViewModel.ReadingDirectoryDbState
import com.example.fullproject.screens.dblists.DataBaseListViewModel.ReadingSongDbState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DataBaseListFragment : Fragment(R.layout.fragment_database_list) {
    private lateinit var binding: FragmentDatabaseListBinding
    private val viewModel: DataBaseListViewModel by viewModels()
    private var popupWindow: PopupWindow? = null

    private val songAdapter by lazy {
        SongDBAdapter(object : SongDBActionListener {
            override fun updateFlag(uri: String, flag: Boolean) {
                viewModel.updateFlagAutoPlaySong(uri, flag)
            }

            override fun deleteElement(id: Long) {
                viewModel.deleteSongElement(id)
            }
        })
    }

    private val directoryAdapter by lazy {
        DirectoryDBAdapter(object : DirectoryDBActionListener {
            override fun updateFlag(uri: String, flag: Boolean) {
                viewModel.updateFlagAddPlaylistDir(uri, flag)
            }

            override fun deleteElement(id: Long) {
                viewModel.deleteDirElement(id)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDatabaseListBinding.inflate(inflater, container, false)
        setupButtonsOnScreen()
        setupAdapters()
        observeListsInViewModel()
        setupListenerChildFragments()

        return binding.root
    }

    private fun setupListenerChildFragments(){
        childFragmentManager.setFragmentResultListener(AddingDirInDbDialog.KEY_ADD_DIRECTORY, this) { _, bundle ->
            val name = bundle.getString(AddingDirInDbDialog.KEY_NAME) ?: ""
            val uri = bundle.getString(AddingDirInDbDialog.KEY_URI) ?: ""
            val disEnableForReading = bundle.getBoolean(AddingDirInDbDialog.KEY_DIS_ENABLED_FOR_READING, false)

            viewModel.writeDirectoryInDB(
                name = name,
                uri = uri,
                disEnableForReading = disEnableForReading)

        }

        childFragmentManager.setFragmentResultListener(AddingSongInDbDialog.KEY_ADD_SONG, this) { _, bundle ->
            val name = bundle.getString(AddingSongInDbDialog.KEY_NAME) ?: ""
            val author = bundle.getString(AddingSongInDbDialog.KEY_AUTHOR) ?: ""
            val uri = bundle.getString(AddingSongInDbDialog.KEY_URI) ?: ""
            val disEnableAutoPlay = bundle.getBoolean(AddingSongInDbDialog.KEY_DIS_ENABLE_AUTO_PLAY, false)

            viewModel.writeSongInDB(
                name = name,
                uri = uri,
                author = author,
                disEnableAutoPlay = disEnableAutoPlay,
            )
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupButtonsOnScreen() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addMusicBtn.setOnClickListener {
            showPopupWindow(it)
        }

        binding.addDirBtn.setOnClickListener {
            openAddDirectoryDialog()
        }
    }

    private fun showPopupWindow(anchorView: View) {
        if (popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
            return
        }

        val unsavedSongs = viewModel.songNotSavedYet.map { it.uri }
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_unsaved_song_list, null)
        val listView: ListView = popupView.findViewById(R.id.popupListView)
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, unsavedSongs)

        val popupWidth = anchorView.width
        val popupHeight = 600

        popupWindow = PopupWindow(popupView, popupWidth, popupHeight, true)

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)

        val buttonTop = location[1] - anchorView.height
        val popupY = buttonTop - popupHeight


        popupWindow?.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], popupY)

        listView.setOnItemClickListener { _, _, position, _ ->
            handleItemClick(unsavedSongs[position])
            popupWindow?.dismiss()
        }
    }

    private fun handleItemClick(itemText: String) {
        openAddSongDialog(itemText)
    }

    private fun openAddSongDialog(uri:String) {
        val dialog = AddingSongInDbDialog.newInstance(uri)
        dialog.show(childFragmentManager, getString(R.string.adding_song_in_db_dialog))
    }

    private fun openAddDirectoryDialog() {
        val dialog = AddingDirInDbDialog()
        dialog.show(childFragmentManager, getString(R.string.adding_dir_in_db_dialog))

    }

    private fun setupAdapters() {
        binding.listDir.layoutManager = LinearLayoutManager(context)
        binding.listDir.adapter = directoryAdapter

        binding.listMusic.layoutManager = LinearLayoutManager(context)
        binding.listMusic.adapter = songAdapter
    }

    private fun observeListsInViewModel() {
        viewModel.listSavedSongs.observe(viewLifecycleOwner) { songDbState ->
            when (songDbState) {
                is ReadingSongDbState.Loading -> listDbSongInLoadingState()
                is ReadingSongDbState.Success -> updateDbListSongOnScreen(songDbState.listSong)
                is ReadingSongDbState.Empty -> updateDbListSongOnScreen(emptyList())
                is ReadingSongDbState.Error -> Unit
            }
        }

        viewModel.listDirectories.observe(viewLifecycleOwner) { directoryDbState ->
            when (directoryDbState) {
                is ReadingDirectoryDbState.Loading -> listDbDirectoryInLoadingState()
                is ReadingDirectoryDbState.Success -> updateDbListDirectoriesOnScreen(directoryDbState.listDirectories)
                is ReadingDirectoryDbState.Empty -> updateDbListDirectoriesOnScreen(emptyList())
                is ReadingDirectoryDbState.Error -> Unit
            }
        }
    }

    private fun updateDbListSongOnScreen(listSong: List<Song>) {
        binding.musicDbPb.visibility = View.GONE
        songAdapter.listOfSongs = listSong
    }

    private fun listDbSongInLoadingState() {
        binding.musicDbPb.visibility = View.VISIBLE
    }

    private fun updateDbListDirectoriesOnScreen(directories: List<Directory>) {
        binding.directoryDbPb.visibility = View.GONE
        directoryAdapter.listOfDirectories = directories
    }

    private fun listDbDirectoryInLoadingState() {
        binding.directoryDbPb.visibility = View.VISIBLE
    }

}