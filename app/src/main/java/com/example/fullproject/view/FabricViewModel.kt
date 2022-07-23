package com.example.fullproject.view

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fullproject.App
import com.example.fullproject.view.MusicPlayerViewModel

class FactoryViewModel(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass){
            MusicPlayerViewModel::class.java -> {
                MusicPlayerViewModel(app)
            }
            MusicListViewModel::class.java -> {
                MusicListViewModel(app)
            }
            else ->{
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}
fun Fragment.factory() = FactoryViewModel(requireContext().applicationContext as App)