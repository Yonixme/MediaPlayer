package com.example.fullproject.model.song.provider.infoprovider

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.model.song.entities.Song
import com.example.fullproject.model.song.entities.SongWithDetails
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

    override fun getInformationForSong(song: Song): SongWithDetails? {
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