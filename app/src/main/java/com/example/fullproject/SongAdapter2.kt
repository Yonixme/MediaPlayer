package com.example.fullproject

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.services.model.SongMusic
import com.example.fullproject.databinding.SongItemBinding
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import com.example.fullproject.services.model.songpack.entities.Song
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface SongActionListener2{
    fun onStartSound(song: SongMusic)

    fun onPauseSound(song: SongMusic)

    fun onStopSound(song: SongMusic)

    fun openMusicPlayer(song: SongMusic)

    fun onSetName()

    suspend fun getSong(): List<MetaDataSong>
}

class SongAdapter2 (private val songActionListener2: SongActionListener2):
    RecyclerView.Adapter<SongAdapter2.SongHolder2>(), View.OnClickListener {
    private lateinit var songs: List<MetaDataSong>
    private var listUri = mutableListOf<String?>()

    private var listSong = listOf<MetaDataSong>()

        init {
            GlobalScope.launch {
                listSong = songActionListener2.getSong()
                for (l in listSong)
                    listUri.add(l.uri)
            }
        }


    class SongHolder2(val binding: SongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder2 {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongItemBinding.inflate(inflater, parent, false)



        binding.itemView.setOnClickListener(this)
        binding.itemMore.setOnClickListener(this)
        binding.launchMusic.setOnClickListener(this)

        return SongAdapter2.SongHolder2(binding)
    }

    override fun onBindViewHolder(holder: SongHolder2, position: Int) {
//        if (song.uri.lastPathSegment.toString() in listUri){
//            Log.d("DataBaseURI", "")
//            val index = listUri.indexOf(song.uri.path)
//            userNameTextView.text = list[index].name ?: list[index].uri
//            authorNameTextView.text = list[index].author ?: list[index].uri
//        }
//        else{
//            userNameTextView.text = song.uri.lastPathSegment
//            authorNameTextView.text = "Author"
//        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}