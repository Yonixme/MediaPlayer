package com.example.fullproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.MusicSongItemBinding
import java.lang.Exception
import java.security.cert.Extension
import kotlin.properties.Delegates

interface SongActionListener{
    fun onStartSound(song: SongMusic)

    fun onPauseSound(song: SongMusic)

    fun onStopSound(song: SongMusic)

    fun onMusicPlaylist(song: SongMusic)

    fun onSetName()
}

class SongAdapter(
    private val songActionListener: SongActionListener
    ): RecyclerView.Adapter<SongAdapter.SongHolder>(), View.OnClickListener
{
    var listSong = mutableListOf<SongMusic>()
//    var listMusic = mutableListOf<Uri>()
    var lastMusic: Int? = null
    private var isPlay = false

    class SongHolder(val binding: MusicSongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listSong.size
//    override fun getItemCount(): Int = listMusic.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MusicSongItemBinding.inflate(inflater, parent, false)

        binding.itemView.setOnClickListener(this)
        binding.itemMore.setOnClickListener(this)
        binding.launchMusic.setOnClickListener(this)

        return SongHolder(binding)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = listSong[position]
        with(holder.binding){
            launchMusic.tag = song
            itemMore.tag = song
            itemView.tag = song
            userNameTextView.text = song.uri.lastPathSegment
            if (song.isPlay) {
                launchMusic.setImageResource(R.drawable.ic_pause)
                song.isPlay = false
            }else{
                launchMusic.setImageResource(R.drawable.ic_play)
                song.isPlay = true
            }
        }
    }

    override fun onClick(v: View) {
        val song = v.tag as SongMusic
        when(v.id){
            R.id.itemMore -> {
                showPopupMenu(v)
            }
            R.id.launchMusic -> {
                val view = v as ImageView
                if (lastMusic != null && lastMusic != listSong.indexOfFirst { it.uri == song.uri }){
                    songActionListener.onStopSound(listSong[lastMusic!!])
                    notifyItemChanged(lastMusic!!)
                }
                if (song.isPlay) {
                    view.setImageResource(R.drawable.ic_pause)
                    songActionListener.onPauseSound(song)
                    song.isPlay = false
                }else{
                    view.setImageResource(R.drawable.ic_play)
                    songActionListener.onStartSound(song)
                    song.isPlay = true
                }
                lastMusic = listSong.indexOfFirst { it.uri == song.uri }
            }
            else -> {
                songActionListener.onMusicPlaylist(song)
            }
        }
    }

    private fun showPopupMenu(view: View){
        val context = view.context
        val popupMenu = PopupMenu(context, view)
        val song = view.tag as SongMusic
        val position = listSong.indexOfFirst { it.uri == song.uri }

        popupMenu.menu.add(0, set_Name, Menu.NONE, "set name for music")

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                set_Name -> {
                    songActionListener.onSetName()
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object{
        private const val set_Name = 1
    }
}