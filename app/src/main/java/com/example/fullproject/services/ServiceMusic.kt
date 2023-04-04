package com.example.fullproject.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class ServiceMusic : Service() {
    private val binder = MyServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class MyServiceBinder : Binder() {
        fun myService(): ServiceMusic{
            return this@ServiceMusic
        }
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
}