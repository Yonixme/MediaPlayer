package com.example.fullproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.fullproject.model.services.MusicService

class MusicBroadcast() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let {action ->
            val intentService = Intent(context, MusicService::class.java).apply {
                this.action = action
            }
            ContextCompat.startForegroundService(context, intentService)
        }
    }
}