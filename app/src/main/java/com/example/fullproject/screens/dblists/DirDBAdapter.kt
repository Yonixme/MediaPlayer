package com.example.fullproject.screens.dblists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fullproject.databinding.BDataItemBinding
import com.example.fullproject.services.model.dirpack.entities.Dir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface DirDBActionListener{
    suspend fun getListDirs(): List<Dir>

    fun updateFlag(id: Long, flag: Boolean)
}

class DirDBAdapter(
    private val dirDBActionListener: DirDBActionListener
): RecyclerView.Adapter<DirDBAdapter.DirDBHolder>(), View.OnClickListener {

    class DirDBHolder(val binding: BDataItemBinding): RecyclerView.ViewHolder(binding.root)
    private var listDir = listOf<Dir>()

    init {
        GlobalScope.launch(Dispatchers.IO) { listDir = dirDBActionListener.getListDirs() }
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
            isAddToList.isChecked = listDir[position].addToStackPlaying ?: true
            isAddToList.setOnCheckedChangeListener{ _, isChecked ->
                dirDBActionListener.updateFlag(dir.id, isChecked)}
        }


    }

    override fun getItemCount(): Int {
        return listDir.size
    }

    override fun onClick(v: View?) {

    }
}