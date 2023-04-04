package com.example.fullproject



import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.fullproject.businesslogic.SoundServiceMusic
import com.example.fullproject.services.BaseServiceMusic
import java.io.File

class App: Application() {

    var listDirWithMusic = mutableListOf<File>(
        File("/storage/emulated/0/Download") ,
        File("/storage/emulated/0/Music"),
        File("/storage/emulated/0/Ringtone")
    )

    var soundServiceMusic = SoundServiceMusic()

    var id = 0

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, BaseServiceMusic::class.java)

        startService(intent)

        Handler(Looper.getMainLooper()).postDelayed({stopService(intent)}, 3000L)

        id = 1
        Log.d("Base Service", "onCreate()")
    }

    fun upValue(){
        id++
    }
}