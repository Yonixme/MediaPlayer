package com.example.fullproject

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.services.model.SongMusic
import com.example.fullproject.databinding.SongItemBinding
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import kotlinx.coroutines.*

interface SongActionListener{
    fun onStartSound(song: SongMusic)

    fun onPauseSound(song: SongMusic)

    fun onStopSound(song: SongMusic)

    fun openMusicPlayer(song: SongMusic)

    fun onSetName()

    suspend fun getSong(): List<MetaDataSong>
}

class SongAdapter(
    private val songActionListener: SongActionListener
    ): RecyclerView.Adapter<SongAdapter.SongHolder>(), View.OnClickListener
{
    private var list = listOf<MetaDataSong>()
    private var listUri = mutableListOf<String?>()

    var listSong = mutableListOf<SongMusic>()
    private var lastMusic: Int? = null

    class SongHolder(val binding: SongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listSong.size

    init {
        GlobalScope.launch(Dispatchers.IO) {
            list = songActionListener.getSong()
            for (l in list) {
                listUri.add(l.uri)
                Log.d("DataBaseURI", l.uri)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongItemBinding.inflate(inflater, parent, false)

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
                Log.d("DataBaseURI", song.uri.path.toString())

                if(song.uri.path.toString() in listUri){
                    Log.d("DataBaseURI", "")
                    val index = listUri.indexOf(song.uri.path.toString())
                    userNameTextView.text = list[index].name ?: Uri.parse(list[index].uri).lastPathSegment.toString()
                    authorNameTextView.text = list[index].author ?: Uri.parse(list[index].uri).lastPathSegment.toString()
                }else{
                    userNameTextView.text = song.uri.lastPathSegment
                    authorNameTextView.text = "Author"
                }
//
//                userNameTextView.text = song.uri.lastPathSegment
//                authorNameTextView.text = "Author"

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
                songActionListener.openMusicPlayer(song)
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

    private suspend fun initLists() = withContext(Dispatchers.IO){
        Repositories.songsRepository.getSongs(true)
            .collect {
                list = it
            }
        for (s in list)
            listUri.add(Uri.parse(s.uri).lastPathSegment.toString())
    }

    companion object{
        private const val set_Name = 1
    }
}