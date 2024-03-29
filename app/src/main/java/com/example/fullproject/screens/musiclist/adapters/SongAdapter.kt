package com.example.fullproject.screens.musiclist.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.R
import com.example.fullproject.databinding.SongItemBinding
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong
import com.example.fullproject.model.Song

interface SongActionListener{
    fun onStartSound(song: Song)

    fun onPauseSound()

    fun onStopSound()

    fun openMusicPlayer(song: Song)

    fun onSetName()

    fun isPlaySound(): Boolean

    fun getSongListWithDB(): List<MetaDataSong>

    fun getCurrentSong(): Song
}

class SongAdapter(
    private val songActionListener: SongActionListener
    ): RecyclerView.Adapter<SongAdapter.SongHolder>(), View.OnClickListener
{
    private var getNameWithDB = mutableListOf<Boolean>()
    private var musicInListPlayed = false
    private var list = listOf<MetaDataSong>()
    private var listUri = mutableListOf<String?>()

    var listSong = listOf<Song>()
    private var indexLastMusic: Int? = null

    class SongHolder(val binding: SongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listSong.size

    init {
        list = songActionListener.getSongListWithDB()
        for (l in list) {
            listUri.add(l.uri)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongItemBinding.inflate(inflater, parent, false)
        getNameWithDB.add(true)

        binding.itemView.setOnClickListener(this)
        binding.itemMore.setOnClickListener(this)
        binding.launchMusic.setOnClickListener(this)

        return SongHolder(binding)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = listSong[position]

        Log.d("DataBaseURI", position.toString())
            with(holder.binding){
                launchMusic.tag = song
                itemMore.tag = song
                itemView.tag = song

                if(song.uri.path.toString() in listUri && list[listUri.indexOf(song.uri.path.toString())].addToStackPlaying
                    && getNameWithDB[position]){
                    val index = listUri.indexOf(song.uri.path.toString())

                    userNameTextView.text = list[index].name
                        ?: Uri.parse(list[index].uri).lastPathSegment.toString()
                    authorNameTextView.text = list[index].author
                        ?: Uri.parse(list[index].uri).lastPathSegment.toString()

                }else{
                    userNameTextView.text = song.uri.lastPathSegment
                    authorNameTextView.text = "Author"
                }
                if (song == songActionListener.getCurrentSong() && songActionListener.isPlaySound()) {
                    launchMusic.setImageResource(R.drawable.ic_pause)
                    indexLastMusic = holder.adapterPosition
                    musicInListPlayed = true
                }
                else launchMusic.setImageResource(R.drawable.ic_play)
            }
    }

    override fun onClick(v: View) {
        val song = v.tag as Song
        when(v.id){
            R.id.itemMore -> {
                showPopupMenu(v)
            }
            R.id.launchMusic -> {
                val view = v as ImageView
                if (indexLastMusic != null && indexLastMusic != listSong.indexOf(song)){
                    songActionListener.onStopSound()
                    notifyItemChanged(indexLastMusic!!)
                    musicInListPlayed = false
                }
                if (musicInListPlayed) {
                    view.setImageResource(R.drawable.ic_play)
                    songActionListener.onPauseSound()
                    musicInListPlayed = false
                }else{
                    view.setImageResource(R.drawable.ic_pause)
                    songActionListener.onStartSound(song)
                    if (indexLastMusic != null)notifyItemChanged(indexLastMusic!!)
                    musicInListPlayed = true
                }
                indexLastMusic = listSong.indexOf(song)
                }
                else -> {
                songActionListener.openMusicPlayer(song)
            }
        }
    }

    private fun showPopupMenu(view: View){
        val context = view.context
        val song = view.tag as Song
        val index = listSong.indexOf(song)
        val popupMenu = PopupMenu(context, view)

        popupMenu.menu.add(0, SET_NAME_ID, Menu.NONE, "set name for music")

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                SET_NAME_ID -> {
                    getNameWithDB[index] = !getNameWithDB[index]
                    notifyItemChanged(index)
                    songActionListener.onSetName()
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object{
        private const val SET_NAME_ID = 1
    }
}