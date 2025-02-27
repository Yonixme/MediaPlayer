package com.example.fullproject.model.services.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fullproject.MusicBroadcast
import com.example.fullproject.R
import com.example.fullproject.model.services.MusicService.Companion.COMMAND_NEXT_SONG
import com.example.fullproject.model.services.MusicService.Companion.COMMAND_ON_PAUSE_MUSIC
import com.example.fullproject.model.services.MusicService.Companion.COMMAND_ON_PLAY_MUSIC
import com.example.fullproject.model.services.MusicService.Companion.COMMAND_ON_STOP_MUSIC
import com.example.fullproject.model.services.MusicService.Companion.COMMAND_PREVIOUS_SONG
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.utils.convertMillisToMinute


class NotificationHelper(
    private val context: Context
) {
    private val channelId = "music_channel"
    private val notificationId = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Music Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
                //lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                //setShowBadge(false)
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }


    fun createNotification(songWithDetails: SongWithDetails?): Notification{
        //val playIntent = createPendingIntent(COMMAND_ON_PLAY_MUSIC)
        //val pauseIntent = createPendingIntent(COMMAND_ON_PAUSE_MUSIC)
        val titleText = songWithDetails?.song?.name ?: "Music Player"
        val stopIntent = createPendingIntent(COMMAND_ON_STOP_MUSIC)
        val nextIntent = createPendingIntent(COMMAND_NEXT_SONG)
        val previousIntent = createPendingIntent(COMMAND_PREVIOUS_SONG)

        val launchIntent =
            if (songWithDetails?.isPlaying == true)
                createPendingIntent(COMMAND_ON_PAUSE_MUSIC)
            else
                createPendingIntent(COMMAND_ON_PLAY_MUSIC)

        println("in notification123 $songWithDetails")
        val launchSource =
            if (songWithDetails?.isPlaying == true)
                R.drawable.ic_pause
            else
                R.drawable.ic_play

        val uri = if (songWithDetails?.song?.uri != null) Uri.parse(songWithDetails.song.uri).lastPathSegment
        else songWithDetails?.song?.uri
        val currentTimeInMinute = convertMillisToMinute(songWithDetails?.currentPosition ?: 0)
        val durationInMinute = convertMillisToMinute(songWithDetails?.duration ?: 0)

        val bigRemoteViews = RemoteViews(context.packageName, R.layout.custom_notification_big).apply {
            setTextViewText(R.id.text, "$uri \t $currentTimeInMinute/$durationInMinute")
            setImageViewResource(R.id.icon, android.R.drawable.ic_media_play)
        }


        listOf(bigRemoteViews).forEach { views ->
            views.apply {
                setOnClickPendingIntent(R.id.btn_prev, previousIntent)
                setImageViewResource(R.id.btn_prev, R.drawable.ic_previous)

                setOnClickPendingIntent(R.id.btn_pause, launchIntent)
                setImageViewResource(R.id.btn_pause, launchSource)

                setOnClickPendingIntent(R.id.btn_next, nextIntent)
                setImageViewResource(R.id.btn_next, R.drawable.ic_next)

                setOnClickPendingIntent(R.id.btn_stop, stopIntent)
                setImageViewResource(R.id.btn_stop, R.drawable.ic_stop)
            }
        }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(titleText)
            .setContentText(uri)
            .setDeleteIntent(stopIntent)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_previous, "prev", previousIntent)
            .addAction(launchSource, "pause", launchIntent)
            .addAction(R.drawable.ic_next, "next", nextIntent)
            .addAction(R.drawable.ic_stop, "stop", stopIntent)
            .setDefaults(0)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
            )
            .setCustomBigContentView(bigRemoteViews)
            .build()

    }

    fun updateNotification(songWithDetails: SongWithDetails?) {
        val notification = createNotification(songWithDetails)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, MusicBroadcast::class.java).apply { this.action = action }
        return PendingIntent.getBroadcast(
            context, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}