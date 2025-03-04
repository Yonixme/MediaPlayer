package com.example.fullproject.model.song.provider.controller

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.model.song.entities.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerMusicController @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicController {
    private var mp: MediaPlayer? = null
    private var playingState: Boolean = false

    override fun playMusic(uri: String) {
        if (mp == null) {
            mp = createMP(uri) ?: return
        }
        if (playingState) return
        mp?.start() ?: return
        playingState = true
    }

    override fun pauseMusic() {
        if (!playingState) return
        mp?.pause() ?: return
        playingState = false
    }

    override fun stopMusic() {
        mp?.stop() ?: return
        mp?.release() ?: return
        mp = null
        playingState = false
    }

    override fun setCurrentTimeInMillis(newTime: Int, block: () -> Unit) {
        mp?.seekTo(newTime)
        mp?.setOnCompletionListener {
            block.invoke()
        }
    }

    override fun setActionOnFinish(block: () -> Unit) {
        mp?.setOnCompletionListener {
            block.invoke()
        }
    }

     private fun createMP(uri: String): MediaPlayer?{
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

    override fun getCurrentPosition(): Int = mp?.currentPosition ?: 0

    override fun getDuration(): Int = mp?.duration ?: 0

    override fun changeSong(uri: String){
        stopMusic()
        playMusic(uri)
    }

    override fun getIsPlayingMusicState(): Boolean = playingState

    override fun pausePlaying() {
        if (playingState)
            mp?.pause()
    }

    override fun continuePlaying() {
        if (playingState)
            mp?.start()
    }

    override fun getInformationForSong(song: Song): SongWithDetails?{
        val mp = createMP(song.uri)
        return if (mp == null){
            null
        }else{
            SongWithDetails(
                song = song,
                duration = mp.duration,
                isPlaying = playingState,
                currentPosition = mp.currentPosition
            )
        }
    }
}