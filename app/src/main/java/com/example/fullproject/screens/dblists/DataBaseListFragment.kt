package com.example.fullproject.screens.dblists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullproject.activityNavigator
import com.example.fullproject.databinding.FragmentDatabaseListBinding
import com.example.fullproject.services.model.dirpack.entities.Dir
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
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

            override suspend fun getListDirs(): List<Dir> {
                return viewModel.getListDir(false)
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