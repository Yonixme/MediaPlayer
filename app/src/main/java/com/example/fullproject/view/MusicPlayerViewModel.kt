package com.example.fullproject.view

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.fullproject.businesslogic.SoundServiceMusic
import java.lang.Exception

class MusicPlayerViewModel(private val array: MutableList<Uri>) : ViewModel() {
    private var soundService: SoundServiceMusic = SoundServiceMusic()

//    fun setContextForMusic(context: Context?){
//            soundService.putContext(context)
//    }


}