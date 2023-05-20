package com.example.fullproject.screens.dblists.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.R
import com.example.fullproject.databinding.BDataItemBinding
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong

interface SongDBActionListener{
    fun getListSong(): List<MetaDataSong>

    fun updateFlag(id: Long, flag: Boolean)

    fun deleteElement(id: Long)
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

    override fun onClick(v: View) {
        val metaSong = v.tag as MetaDataSong

        when(v.id){
            R.id.item_more ->{
                showPopupMenu(v, metaSong)
            }
            else ->{

            }
        }
    }

    private fun showPopupMenu(view: View, song: MetaDataSong){
        val context: Context = view.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.menu.add(0, DELETE_ID, Menu.NONE, "Delete")

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                DELETE_ID -> {
                    songDBActionListener.deleteElement(song.id)
                    listSong = songDBActionListener.getListSong()
                    notifyDataSetChanged()
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object{
        @JvmStatic
        private final val DELETE_ID = 1
    }
}