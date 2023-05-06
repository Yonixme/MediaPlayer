package com.example.fullproject

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.fullproject.services.model.SoundServiceMusic
import com.example.fullproject.services.ServiceMusic
import java.io.File

class App: Application() {

    private lateinit var mService: ServiceMusic
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as ServiceMusic.MyServiceBinder
            mService = binder.myService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    var soundServiceMusic = SoundServiceMusic()

    override fun onCreate() {
        super.onCreate()

        Intent(this, ServiceMusic::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        Log.d("Base Service", "onCreate()")
    }

    fun stopService(){
        unbindService(connection)
        mBound = false
    }

    fun getMusicService(): ServiceMusic = mService
}