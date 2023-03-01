package com.example.fullproject.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.fullproject.App
import com.example.fullproject.businesslogic.SongMusic

class BaseServiceMusic : Service() {
    var serviceIsRun = false

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Base Service", "Create")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(serviceIsRun) return super.onStartCommand(intent, flags, startId)
        serviceIsRun = true
        Log.d("Base Service", "Start")
                object : CountDownTimer(10000L, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("Base Service", "Task1")
            }

            override fun onFinish() {
                Log.d("Base Service", "Task2")
                stopSelf()
            }
        }.start()


//        Handler(Looper.getMainLooper()).postDelayed({stopSelf()}, 3000L)
        return START_NOT_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (serviceIsRun) serviceIsRun = false
        Log.d("Base Service", "Destroy")
        super.onDestroy()
        Log.d("Base Service", "Destroy")
    }

    fun play(context: Context, song: SongMusic){
        (applicationContext as App).soundServiceMusic.onPlaySound(context, song)
    }
}