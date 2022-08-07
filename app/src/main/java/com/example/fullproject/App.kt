package com.example.fullproject

import android.app.Application
import com.example.fullproject.businesslogic.SoundServiceMusic

class App: Application() {
    var soundServiceMusic = SoundServiceMusic()
}