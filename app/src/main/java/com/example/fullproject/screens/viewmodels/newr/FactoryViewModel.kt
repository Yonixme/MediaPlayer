package com.example.fullproject.screens.viewmodels.newr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import com.example.fullproject.App
import com.example.fullproject.screens.dblists.oldr.DataBaseListViewModelOLD
import com.example.fullproject.screens.musiclist.oldr.MusicListViewModelOLD
import com.example.fullproject.screens.musicplayer.oldr.MusicPlayerViewModelOLD

class FactoryViewModel(private val app: App) : Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass){
            MusicPlayerViewModelOLD::class.java -> {
                MusicPlayerViewModelOLD(app)
            }
            MusicListViewModelOLD::class.java -> {
                MusicListViewModelOLD(app)
            }
            DataBaseListViewModelOLD::class.java ->{
                DataBaseListViewModelOLD(app)
            }
            else ->{
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}