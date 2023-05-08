package com.example.fullproject.screens.dblists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.databinding.BDataItemBinding
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import kotlinx.coroutines.*

interface SongDBActionListener{
    fun getListSong(): List<MetaDataSong>

    fun updateFlag(id: Long, flag: Boolean)
}

class SongDBAdapter(
    private val songDBActionListener: SongDBActionListener
    ):RecyclerView.Adapter<SongDBAdapter.SongDBHolder>(), View.OnClickListener {

    class SongDBHolder(val binding: BDataItemBinding): RecyclerView.ViewHolder(binding.root)
    private var listSong = listOf<MetaDataSong>()

    init {
         listSong = songDBActionListener.getListSong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongDBHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BDataItemBinding.inflate(inflater, parent, false)

        binding.itemMore.setOnClickListener(this)
        binding.item.setOnClickListener(this)


        return SongDBHolder(binding)
    }

    override fun onBindViewHolder(holder: SongDBHolder, position: Int) {
        val metaSong = listSong[position]

        with(holder.binding){
            item.tag = metaSong
            itemMore.tag = metaSong

            nameItem.text = listSong[position].name ?: "Null"
            authorItem.text = listSong[position].author ?: "Null"
            uriItem.text = listSong[position].uri
            isAddToList.isChecked = listSong[position].addToStackPlaying
            isAddToList.setOnCheckedChangeListener{ _, isChecked ->
                songDBActionListener.updateFlag(metaSong.id, isChecked)}
        }


    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    override fun onClick(v: View?) {

    }
}