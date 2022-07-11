package com.example.fullproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.MusicSongItemBinding
import java.lang.Exception
import java.security.cert.Extension
import kotlin.properties.Delegates

interface SongActionListener{
    fun onStartAndPauseSound()

//    fun onMusicPlaylist(uri: Uri)
    fun onMusicPlaylist()
}

class SongAdapter(
    private val songActionListener: SongActionListener
    ): RecyclerView.Adapter<SongAdapter.SongHolder>(), View.OnClickListener
{
    var listMusic = mutableListOf<Uri>()
    var lastMusic: Int? = null

    class SongHolder(val binding: MusicSongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listMusic.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MusicSongItemBinding.inflate(inflater, parent, false)

        binding.itemView.setOnClickListener(this)
        binding.itemMore.setOnClickListener(this)
        binding.launchMusic.setOnClickListener(this)

        return SongHolder(binding)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = listMusic[position]
        with(holder.binding){
            launchMusic.tag = song
            itemMore.tag = song
            itemView.tag = song
            userNameTextView.text = song.lastPathSegment

            launchMusic.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onClick(v: View) {
        //val uri = v.tag as Uri
        when(v.id){
            R.id.itemMore -> {
                songActionListener.onStartAndPauseSound()
            }
            R.id.launchMusic -> {
                songActionListener.onStartAndPauseSound()
            }
            else -> {
                songActionListener.onMusicPlaylist()
            }
        }
    }


}