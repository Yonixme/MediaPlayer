package com.example.fullproject.screens.musiclist.newr

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.R
import com.example.fullproject.databinding.SongItemBinding
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails
import com.example.fullproject.model.songpack.entities.MetaDataSong
import com.example.fullproject.screens.musiclist.oldr.SongActionListener

interface SongActionListenerNew{
    fun onPlay(uri: String)

    fun onPause(uri: String)

    fun onStop(uri: String)

    fun getIsPlaying() : Boolean

    fun openScreenWithDetails(uri: String)
}

class SongDiffCallback(
    private val oldList: List<SongWithDetails>,
    private val newList: List<SongWithDetails>
) : DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSong = oldList[oldItemPosition]
        val newSong = newList[newItemPosition]
        return if (oldSong.song.id >= 0 && newSong.song.id >= 0){
            oldSong.song.id == newSong.song.id
        }else{
            oldSong.song.uri == newSong.song.uri
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSong = oldList[oldItemPosition]
        val newSong = newList[newItemPosition]
        return oldSong == newSong
    }
}

class SongAdapterNew(
    private val songActionListener: SongActionListenerNew
): RecyclerView.Adapter<SongAdapterNew.SongHolder>(), View.OnClickListener
{
    var listSongWithDetails: List<SongWithDetails> = emptyList()
        set(newValue){
            val diffCallback = SongDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class SongHolder(val binding: SongItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listSongWithDetails.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongItemBinding.inflate(inflater, parent, false)

        binding.itemView.setOnClickListener(this)
        binding.itemMore.setOnClickListener(this)
        binding.launchMusic.setOnClickListener(this)

        return SongHolder(binding)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val songWithDetails = listSongWithDetails[position]

        Log.d("DataBaseURI", position.toString())
        with(holder.binding) {
            launchMusic.tag = songWithDetails
            itemMore.tag = songWithDetails
            itemView.tag = songWithDetails

            userNameTextView.text = songWithDetails.song.name ?: songWithDetails.song.uri
            authorNameTextView.text = songWithDetails.song.author ?: itemView.context.getString(R.string.author)

            if (songWithDetails.isPlaying)
                launchMusic.setImageResource(R.drawable.ic_pause)
            else
                launchMusic.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onClick(v: View) {
        val songWithDetails = v.tag as SongWithDetails

        when(v.id){
            R.id.launchMusic -> {
                if (songWithDetails.isPlaying){
                    songActionListener.onPause(songWithDetails.song.uri)
                } else {
                    songActionListener.onPlay(songWithDetails.song.uri)
                }
            }
            R.id.itemView -> {
                songActionListener.openScreenWithDetails(songWithDetails.song.uri)
            }

        }
    }
}