package com.example.fullproject.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import com.example.fullproject.App

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