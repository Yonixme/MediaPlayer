package com.example.fullproject

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.MusicSongItemBinding

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
    private var lastMusic: Int? = null

    class SongHolder(val binding: MusicSongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listSong.size

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
            }else{
                launchMusic.setImageResource(R.drawable.ic_play)
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
                    view.setImageResource(R.drawable.ic_play)
                    songActionListener.onPauseSound(song)
                }else{
                    view.setImageResource(R.drawable.ic_pause)
                    songActionListener.onStartSound(song)
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