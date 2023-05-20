package com.example.fullproject

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.fullproject.model.services.ServiceMusic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class App: Application() {
    private var mService = ServiceMusic()
    var mBound: Boolean = false
    private set

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder){
            val binder = service as ServiceMusic.MyServiceBinder
            mService = binder.myService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate() {
        super.onCreate()

        Intent(this, ServiceMusic::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        startServiceAsync(Intent(this, ServiceMusic::class.java))
    }

    private fun startServiceAsync(intent: Intent) = runBlocking(Dispatchers.Default){

        intent.also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun stopService(){
        unbindService(connection)
        mBound = false
    }

    fun getMusicService(): ServiceMusic {
        return mService
    }
}