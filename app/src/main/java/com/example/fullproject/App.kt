package com.example.fullproject

import android.app.Application
import android.net.Uri
import com.example.fullproject.businesslogic.SoundServiceMusic
import java.io.File

class App: Application() {
    var soundServiceMusic = SoundServiceMusic()
}