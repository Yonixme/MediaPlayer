package com.example.fullproject.model.song.provider.infoprovider

import android.content.Context
import android.media.AudioAttributes
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
    private fun createMediaPlayer(uri: String): MediaPlayer?{
        val createdMP = try {
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(context, Uri.parse(uri))
                prepare()
            }
        } catch (e: Exception){
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
}