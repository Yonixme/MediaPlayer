package com.example.fullproject

import android.app.Application
import android.util.Log
import com.example.fullproject.businesslogic.SoundServiceMusic

class App: Application() {
    var id = 0
    var soundServiceMusic = SoundServiceMusic()

    override fun onCreate() {
        super.onCreate()
        id = 1
        Log.d("app", "onCreate()")
    }



    fun upValue(){
        id++
    }
}