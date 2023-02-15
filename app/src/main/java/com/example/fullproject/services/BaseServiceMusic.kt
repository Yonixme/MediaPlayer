package com.example.fullproject.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.fullproject.App
import com.example.fullproject.businesslogic.SongMusic

class BaseServiceMusic : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun play(context: Context, song: SongMusic){
        (applicationContext as App).soundServiceMusic.onPlaySound(context, song)
    }
}