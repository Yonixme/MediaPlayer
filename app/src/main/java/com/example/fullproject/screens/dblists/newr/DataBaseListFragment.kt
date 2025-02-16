package com.example.fullproject.screens.dblists.newr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.R
import com.example.fullproject.databinding.FragmentDatabaseListBinding
import com.example.fullproject.model.room.directory.entities.DirectoryNew
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.screens.dblists.newr.DataBaseListViewModelNew.*
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
//class DataBaseListFragment: Fragment(R.layout.fragment_database_list) {
//    private lateinit var binding: FragmentDatabaseListBinding
//    private lateinit var songAdapter: SongDBAdapterNew
//    private lateinit var directoryAdapter: DirectoryDBAdapterNew
//    private val viewModel: DataBaseListViewModelNew by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        binding = FragmentDatabaseListBinding.inflate(inflater, container, false)
//
//        binding.backBtn.setOnClickListener {
//            findNavController().popBackStack()
//        }
//
//        songAdapter = SongDBAdapterNew(object : SongDBActionListenerNew {
//            override fun updateFlag(uri: String, flag: Boolean) {
//                viewModel.updateFlagAutoPlaySong(uri, flag)
//            }
//
//            override fun deleteElement(id: Long) {
//                viewModel.deleteSongElement(id)
//            }
//        }
//        )
//
//        directoryAdapter = DirectoryDBAdapterNew(object : DirectoryDBActionListenerNew {
//            override fun updateFlag(uri: String, flag: Boolean) {
//                viewModel.updateFlagAddPlaylistDir(uri, flag)
//            }
//
//            override fun deleteElement(id: Long) {
//                viewModel.deleteDirElement(id)
//            }
//        }
//        )
//        binding.listDir.layoutManager = LinearLayoutManager(context)
//        binding.listDir.adapter = directoryAdapter
//
//        binding.listMusic.layoutManager = LinearLayoutManager(context)
//        binding.listMusic.adapter = songAdapter
//
//        binding.addItemInBd.addMusicBtn.setOnClickListener{
//            val name: String? = if(binding.addItemInBd.nameItem.text.toString().isBlank()) null else binding.addItemInBd.nameItem.text.toString()
//            val author: String? = if (binding.addItemInBd.authorItem.text.toString().isBlank()) null else binding.addItemInBd.authorItem.text.toString()
//            val uri = binding.addItemInBd.uriItem.text.toString()
//            val disEnableAutoPlay = binding.addItemInBd.isAddToList.isChecked
//
//            viewModel.writeSongInDB(uri, name, author, disEnableAutoPlay)
//            updateUI()
//        }
//
//        binding.addItemInBd.addDirBtn.setOnClickListener{
//            val name: String? = if(binding.addItemInBd.nameItem.text.toString().isBlank()) null else binding.addItemInBd.nameItem.text.toString()
//            val uri = binding.addItemInBd.uriItem.text.toString()
//            val addToStack = binding.addItemInBd.isAddToList.isChecked
//            updateUI()
//
//            viewModel.writeDirectoryInDB(uri,name,addToStack)
//            updateUI()
//        }
//
//        viewModel.listSongs.observe(viewLifecycleOwner){songDbState ->
//            when (songDbState) {
//                is ReadingSongDbState.Loading -> {
//                    listDbSongInLoadingState()
//                }
//
//                is ReadingSongDbState.Success -> {
//                    updateDbListSongOnScreen(songDbState.listSong)
//                    println("Dirs123 ${songDbState.listSong}")
//                }
//
//                is ReadingSongDbState.Empty -> {
//                    updateDbListSongOnScreen(emptyList())
//                }
//
//                is ReadingSongDbState.Error -> {}
//            }
//
//        }
//
//        viewModel.listDirectories.observe(viewLifecycleOwner){directoryDbState ->
//            when (directoryDbState) {
//                is ReadingDirectoryDbState.Loading -> {
//                    listDbDirectoryInLoadingState()
//                }
//
//                is ReadingDirectoryDbState.Success -> {
//                    updateDbListDirectoriesOnScreen(directoryDbState.listDirectories)
//                }
//
//                is ReadingDirectoryDbState.Empty -> {
//                    updateDbListSongOnScreen(emptyList())
//                }
//
//                is ReadingDirectoryDbState.Error -> {
//                }
//            }
//
//        }
//
//        updateUI()
//        return binding.root
//    }
//    private fun updateDbListSongOnScreen(listSong: List<SongNew>){
//        songAdapter.listOfSongs = listSong
//    }
//
//    private fun listDbSongInLoadingState(){ }
//
//    private fun updateDbListDirectoriesOnScreen(directories: List<DirectoryNew>){
//        directoryAdapter.listOfDirectories = directories
//    }
//
//    private fun listDbDirectoryInLoadingState(){ }
//
//    private fun updateUI(){
//        binding.addItemInBd.nameItem.setText("")
//        binding.addItemInBd.authorItem.setText("")
//        binding.addItemInBd.uriItem.setText(getString(R.string.root_path))
//        binding.addItemInBd.isAddToList.isChecked = false
//    }
//
//}

@AndroidEntryPoint
class DataBaseListFragment : Fragment(R.layout.fragment_database_list) {
    private lateinit var binding: FragmentDatabaseListBinding
    private val viewModel: DataBaseListViewModelNew by viewModels()

    private val songAdapter by lazy {
        SongDBAdapterNew(object : SongDBActionListenerNew {
            override fun updateFlag(uri: String, flag: Boolean) {
                viewModel.updateFlagAutoPlaySong(uri, flag)
            }

            override fun deleteElement(id: Long) {
                viewModel.deleteSongElement(id)
            }
        })
    }

    private val directoryAdapter by lazy {
        DirectoryDBAdapterNew(object : DirectoryDBActionListenerNew {
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
        updateUI()
        return binding.root
    }

    private fun setupButtonsOnScreen() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addItemInBd.addMusicBtn.setOnClickListener {
            addSongToDatabase()
        }

        binding.addItemInBd.addDirBtn.setOnClickListener {
            addDirectoryToDatabase()
        }
    }

    private fun setupAdapters() {
        binding.listDir.layoutManager = LinearLayoutManager(context)
        binding.listDir.adapter = directoryAdapter

        binding.listMusic.layoutManager = LinearLayoutManager(context)
        binding.listMusic.adapter = songAdapter
    }

    private fun observeListsInViewModel() {
        viewModel.listSongs.observe(viewLifecycleOwner) { songDbState ->
            when (songDbState) {
                is ReadingSongDbState.Loading -> listDbSongInLoadingState()
                is ReadingSongDbState.Success -> updateDbListSongOnScreen(songDbState.listSong)
                is ReadingSongDbState.Empty -> updateDbListSongOnScreen(emptyList())
                is ReadingSongDbState.Error -> {  }
            }
        }

        viewModel.listDirectories.observe(viewLifecycleOwner) { directoryDbState ->
            when (directoryDbState) {
                is ReadingDirectoryDbState.Loading -> listDbDirectoryInLoadingState()
                is ReadingDirectoryDbState.Success -> updateDbListDirectoriesOnScreen(directoryDbState.listDirectories)
                is ReadingDirectoryDbState.Empty -> updateDbListDirectoriesOnScreen(emptyList())
                is ReadingDirectoryDbState.Error -> {  }
            }
        }
    }

    private fun addSongToDatabase() {
        val name = binding.addItemInBd.nameItem.text.toString().takeIf { it.isNotBlank() }
        val author = binding.addItemInBd.authorItem.text.toString().takeIf { it.isNotBlank() }
        val uri = binding.addItemInBd.uriItem.text.toString()
        val disEnableAutoPlay = binding.addItemInBd.isAddToList.isChecked

        viewModel.writeSongInDB(uri, name, author, disEnableAutoPlay)
        updateUI()
    }

    private fun addDirectoryToDatabase() {
        val name = binding.addItemInBd.nameItem.text.toString().takeIf { it.isNotBlank() }
        val uri = binding.addItemInBd.uriItem.text.toString()
        val addToStack = binding.addItemInBd.isAddToList.isChecked

        viewModel.writeDirectoryInDB(uri, name, addToStack)
        updateUI()
    }

    private fun updateDbListSongOnScreen(listSong: List<SongNew>) {
        songAdapter.listOfSongs = listSong
    }

    private fun listDbSongInLoadingState() { }

    private fun updateDbListDirectoriesOnScreen(directories: List<DirectoryNew>) {
        directoryAdapter.listOfDirectories = directories
    }

    private fun listDbDirectoryInLoadingState() { }

    private fun updateUI() {
        binding.addItemInBd.apply {
            nameItem.setText("")
            authorItem.setText("")
            uriItem.setText(getString(R.string.root_path))
            isAddToList.isChecked = false
        }
    }
}