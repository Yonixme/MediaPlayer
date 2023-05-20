package com.example.fullproject.screens.dblists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.databinding.FragmentDatabaseListBinding
import com.example.fullproject.screens.viewmodel.DataBaseListViewModel
import com.example.fullproject.model.sqlite.dirpack.entities.Dir
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong
import com.example.fullproject.screens.dblists.adapters.DirDBActionListener
import com.example.fullproject.screens.dblists.adapters.DirDBAdapter
import com.example.fullproject.screens.dblists.adapters.SongDBActionListener
import com.example.fullproject.screens.dblists.adapters.SongDBAdapter
import com.example.fullproject.utils.activityNavigator
import com.example.fullproject.utils.factory

class DataBaseListFragment : Fragment() {
    private lateinit var binding: FragmentDatabaseListBinding
    private lateinit var songAdapter: SongDBAdapter
    private lateinit var dirAdapter: DirDBAdapter
    private val viewModel: DataBaseListViewModel by viewModels { factory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentDatabaseListBinding.inflate(inflater, container,false)

        binding.backBtn.setOnClickListener{
            activityNavigator().goBack()
        }

        binding.doneMusic.setOnClickListener{
            updateUI()
        }

        binding.addItemInBd.addMusicBtn.setOnClickListener{
            val name: String? = if(binding.addItemInBd.nameItem.text.toString().isBlank()) null else binding.addItemInBd.nameItem.text.toString()
            val author: String? = if (binding.addItemInBd.authorItem.text.toString().isBlank()) null else binding.addItemInBd.authorItem.text.toString()
            val uri = binding.addItemInBd.uriItem.text.toString()
            val addToStack = binding.addItemInBd.isAddToList.isChecked

            viewModel.writeSongInDB(uri, name, author, addToStack)
            updateUI()
        }

        binding.addItemInBd.addDirBtn.setOnClickListener{
            val name: String? = if(binding.addItemInBd.nameItem.text.toString().isBlank()) null else binding.addItemInBd.nameItem.text.toString()
            val uri = binding.addItemInBd.uriItem.text.toString()
            val addToStack = binding.addItemInBd.isAddToList.isChecked

            viewModel.writeDirInDB(uri,name,addToStack)
            updateUI()
        }
        updateUI()
        return binding.root
    }

    private fun updateListSong(){
        songAdapter = SongDBAdapter(object : SongDBActionListener{
            override fun updateFlag(id: Long, flag: Boolean) {
                viewModel.updateFlagAutoPlaySong(id, flag)
            }

            override fun getListSong(): List<MetaDataSong> {
               return viewModel.getListSong(false)
            }

            override fun deleteElement(id: Long) {
                viewModel.deleteSongElement(id)
            }
        })
        val layoutManager = LinearLayoutManager(context)
        binding.listMusic.layoutManager = layoutManager
        binding.listMusic.adapter = songAdapter
    }

    private fun updateListDir(){
        dirAdapter = DirDBAdapter(object : DirDBActionListener{
            override fun updateFlag(id: Long, flag: Boolean) {
                viewModel.updateFlagAddPlaylistDir(id, flag)
            }

            override fun getListDirs(): List<Dir> {
                return viewModel.getListDir(false)
            }

            override fun deleteElement(id: Long) {
                viewModel.deleteDirElement(id)
            }
        })
        val layoutManager = LinearLayoutManager(context)
        binding.listDir.layoutManager = layoutManager
        binding.listDir.adapter = dirAdapter
    }

    private fun updateUI(){
        updateListSong()
        updateListDir()
    }
}