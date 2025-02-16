package com.example.fullproject.model.room.song.infoprovider

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicInfoProvider {
    private var currentMP: MediaPlayer? = null
    private var lastMP: MediaPlayer? = null

    override fun createMediaPlayer(uri: String): MediaPlayer?{
        val createdMP = try {
            MediaPlayer.create(context, Uri.parse(uri))
        } catch (e: Exception){
            println("Error debug ${e.message}")
            null
        }
        return createdMP
    }

    override fun getInformationForSong(song: SongNew): SongWithDetails? {
        val mp = createMediaPlayer(song.uri)
        return if (mp == null){
            null
        }else{
            SongWithDetails(
                song = song,
                duration = mp.duration,
                isPlaying = mp.isPlaying,
                currentPosition = mp.currentPosition
            )
        }
    }

    override fun changeCurrentMediaPlayer(uri: String){
        lastMP = currentMP
        currentMP = createMediaPlayer(uri)
    }

    override fun getCurrentMediaPlayer(): MediaPlayer?{
        return currentMP
    }


}