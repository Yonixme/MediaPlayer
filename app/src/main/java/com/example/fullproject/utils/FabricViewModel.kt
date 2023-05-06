package com.example.fullproject.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import com.example.fullproject.App
import com.example.fullproject.screens.dblists.DataBaseListViewModel
import com.example.fullproject.screens.musiclist.MusicListViewModel
import com.example.fullproject.screens.musicplayer.MusicPlayerViewModel

class FactoryViewModel(private val app: App) : Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass){
            MusicPlayerViewModel::class.java -> {
                MusicPlayerViewModel(app)
            }
            MusicListViewModel::class.java -> {
                MusicListViewModel(app)
            }
            DataBaseListViewModel::class.java ->{
                DataBaseListViewModel(app)
            }
            else ->{
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}
fun Fragment.factory() = FactoryViewModel(requireContext().applicationContext as App)