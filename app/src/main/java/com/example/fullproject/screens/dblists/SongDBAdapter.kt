package com.example.fullproject.screens.dblists

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.R
import com.example.fullproject.databinding.BDataItemBinding
import com.example.fullproject.model.song.entities.Song

interface SongDBActionListener{
    fun updateFlag(uri: String, flag: Boolean)

    fun deleteElement(id: Long)
}

class DbSongDiffCallBack(
    private val oldList: List<Song>,
    private val newList: List<Song>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSong = oldList[oldItemPosition]
        val newSong = newList[newItemPosition]
        return if (oldSong.id >= 0 && newSong.id >= 0){
            oldSong.id == newSong.id
        }else{
            oldSong.uri == newSong.uri
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldSong = oldList[oldItemPosition]
        val newSong = newList[newItemPosition]
        return oldSong == newSong
    }
}

class SongDBAdapter(
    private val songDBActionListener: SongDBActionListener
) : RecyclerView.Adapter<SongDBAdapter.SongDBHolder>(), View.OnClickListener{

    var listOfSongs: List<Song> = emptyList()
        set(newValue){
            val diffCallback = DbSongDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class SongDBHolder(val binding: BDataItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listOfSongs.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongDBHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BDataItemBinding.inflate(inflater, parent, false)

        binding.itemMore.setOnClickListener(this)
        binding.item.setOnClickListener(this)

        return SongDBHolder(binding)
    }

    override fun onBindViewHolder(holder: SongDBHolder, position: Int) {
        val song = listOfSongs[position]

        with(holder.binding) {
            item.tag = song
            itemMore.tag = song

            if (song.disEnableAutoPlay) {
                nameItem.text = buildString {
                    append(song.name ?: itemView.context.getString(R.string.unnamed))
                    append(itemView.context.getString(R.string.skip))
                }
            }else{
                nameItem.text = song.name ?: itemView.context.getString(R.string.unnamed)
            }

            authorItem.text = song.author ?: itemView.context.getString(R.string.author)
            uriItem.text = song.uri

            isAddToList.setOnCheckedChangeListener(null)
            isAddToList.isChecked = song.disEnableAutoPlay

            isAddToList.setOnCheckedChangeListener { _, isChecked ->
                songDBActionListener.updateFlag(song.uri, isChecked)
            }


        }
    }



    override fun onClick(v: View) {
        val song = v.tag as Song

        when(v.id){
            R.id.item_more ->{
                showPopupMenu(v, song)
            }
            else -> Unit
        }
    }

    private fun showPopupMenu(view: View, song: Song){
        val context: Context = view.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.menu.add(0, DELETE_ID, Menu.NONE, context.getString(R.string.delete))

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                DELETE_ID -> {
                    songDBActionListener.deleteElement(song.id)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object{
        private const val DELETE_ID = 1
    }
}