package com.example.fullproject

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.SongItemBinding

interface SongActionListener2{
    fun onStartSound(song: SongMusic)

    fun onPauseSound(song: SongMusic)

    fun onStopSound(song: SongMusic)

    fun openMusicPlayer(song: SongMusic)

    fun onSetName()
}

class SongAdapter2 (private val songActionListener2: SongActionListener2):
    RecyclerView.Adapter<SongAdapter2.SongHolder2>(), View.OnClickListener {

    class SongHolder2(val binding: SongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder2 {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SongHolder2, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}