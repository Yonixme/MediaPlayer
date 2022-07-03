package com.example.fullproject

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.MusicListItemBinding

class SongAdapter: RecyclerView.Adapter<SongAdapter.SongHolder>(){
    private val listMusic = mutableListOf<SongMusic>()


    class SongHolder(binding: MusicListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

}