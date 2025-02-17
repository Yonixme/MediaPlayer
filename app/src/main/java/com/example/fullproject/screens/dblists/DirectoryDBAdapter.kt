package com.example.fullproject.screens.dblists

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.R
import com.example.fullproject.databinding.BDataItemBinding
import com.example.fullproject.model.directory.entities.DirectoryNew

interface DirectoryDBActionListener{
    fun updateFlag(uri: String, flag: Boolean)

    fun deleteElement(id: Long)
}

class DbDirectoryDiffCallBack(
    private val oldList: List<DirectoryNew>,
    private val newList: List<DirectoryNew>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDirectory = oldList[oldItemPosition]
        val newDirectory = newList[newItemPosition]
        return if (oldDirectory.id >= 0 && newDirectory.id >= 0){
            oldDirectory.id == newDirectory.id
        }else{
            oldDirectory.uri == newDirectory.uri
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldDirectory = oldList[oldItemPosition]
        val newDirectory = newList[newItemPosition]
        return oldDirectory == newDirectory
    }
}

class DirectoryDBAdapter(
    private val dirDBActionListener: DirectoryDBActionListener
) : RecyclerView.Adapter<DirectoryDBAdapter.DirectoryDBHolder>(), View.OnClickListener{

    var listOfDirectories: List<DirectoryNew> = emptyList()
        set(newValue){
            val diffCallback = DbDirectoryDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class DirectoryDBHolder(val binding: BDataItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = listOfDirectories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryDBHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BDataItemBinding.inflate(inflater, parent, false)


        binding.itemMore.setOnClickListener(this)
        binding.item.setOnClickListener(this)


        return DirectoryDBHolder(binding)
    }

    override fun onBindViewHolder(holder: DirectoryDBHolder, position: Int) {
        val directory = listOfDirectories[position]

        with(holder.binding){
            item.tag = directory
            itemMore.tag = directory

            nameItem.text = directory.name ?: itemView.context.getString(R.string.unnamed)

            authorItem.visibility = View.GONE
            uriItem.text = directory.uri
            if(directory.isDefaultDir) itemMore.visibility = View.INVISIBLE
            else itemMore.visibility = View.VISIBLE

            isAddToList.setOnCheckedChangeListener(null)
            isAddToList.isChecked = directory.disEnableForReading

            isAddToList.setOnCheckedChangeListener{ _, isChecked ->
                dirDBActionListener.updateFlag(directory.uri, isChecked)}

            if (directory.disEnableForReading) {
                nameItem.paintFlags = nameItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                uriItem.paintFlags = uriItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else {
                nameItem.paintFlags = nameItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                uriItem.paintFlags = uriItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }


    override fun onClick(v: View) {
        val dir = v.tag as DirectoryNew
        when(v.id){
            R.id.item_more ->{
                showPopupMenu(v, dir)
            }
            else -> Unit
        }
    }

    private fun showPopupMenu(view: View, dir: DirectoryNew){
        val context: Context = view.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.menu.add(0, DELETE_ID, Menu.NONE, "Delete")


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                DELETE_ID -> {
                    dirDBActionListener.deleteElement(dir.id)
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