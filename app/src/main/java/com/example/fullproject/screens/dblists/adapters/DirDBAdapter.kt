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
import com.example.fullproject.model.sqlite.dirpack.entities.Dir

interface DirDBActionListener{
    fun getListDirs(): List<Dir>

    fun updateFlag(id: Long, flag: Boolean)

    fun deleteElement(id: Long)
}

class DirDBAdapter(
    private val dirDBActionListener: DirDBActionListener
): RecyclerView.Adapter<DirDBAdapter.DirDBHolder>(), View.OnClickListener {

    class DirDBHolder(val binding: BDataItemBinding): RecyclerView.ViewHolder(binding.root)
    private var listDir = listOf<Dir>()

    init {
        listDir = dirDBActionListener.getListDirs()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirDBHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BDataItemBinding.inflate(inflater, parent, false)

        binding.itemMore.setOnClickListener(this)
        binding.item.setOnClickListener(this)


        return DirDBHolder(binding)
    }

    override fun onBindViewHolder(holder: DirDBHolder, position: Int) {
        val dir = listDir[position]

        with(holder.binding){
            item.tag = dir
            itemMore.tag = dir

            nameItem.text = listDir[position].name ?: "Null"
            authorItem.visibility = View.GONE
            uriItem.text = listDir[position].uri
            if(dir.isPrimaryDir == true) itemMore.visibility = View.INVISIBLE
            isAddToList.isChecked = listDir[position].addToStackPlaying ?: true
            isAddToList.setOnCheckedChangeListener{ _, isChecked ->
                dirDBActionListener.updateFlag(dir.id, isChecked)}
        }


    }

    override fun getItemCount(): Int {
        return listDir.size
    }

    override fun onClick(v: View) {
        val dir = v.tag as Dir

        when(v.id){
            R.id.item_more ->{
                showPopupMenu(v, dir)
            }
            else ->{

            }
        }
    }

    private fun showPopupMenu(view: View, dir: Dir){
        val context: Context = view.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.menu.add(0, DELETE_ID, Menu.NONE, "Delete")


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                DELETE_ID -> {
                    dirDBActionListener.deleteElement(dir.id)
                    listDir = dirDBActionListener.getListDirs()
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