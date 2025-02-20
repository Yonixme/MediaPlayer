package com.example.fullproject

import androidx.lifecycle.ViewModel
import com.example.fullproject.model.services.MusicServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val serviceManager: MusicServiceManager
) : ViewModel() {

    fun init(){

    }

    override fun onCleared() {
        super.onCleared()
        println("destroy 12342 viewmodel")
        serviceManager.unBindService()
    }
}