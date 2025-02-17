package com.example.fullproject.model.song.provider.controller

import android.content.Context
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
            mp = createMP(uri)
        }
        val currentMP = mp ?: return
        if (playingState) return

        mp?.start()
        playingState = true
    }

    override fun pauseMusic() {
        val currentMP = mp ?: return
        if (!playingState) return
        mp!!.pause()
        playingState = false
    }

    override fun stopMusic() {
        if (mp == null) return
        mp?.stop()
        mp?.release()
        mp = null
        playingState = false
    }

    override fun setCurrentTimeInMillis(newTime: Int) {
        mp?.seekTo(newTime)
    }

    override fun setActionOnFinish(block: () -> Unit) {
        mp?.setOnCompletionListener {
            block.invoke()
        }
    }

    private fun createMP(uri: String): MediaPlayer?{
        val createdMP = try {
            MediaPlayer.create(context, Uri.parse(uri))
        } catch (e: Exception){
            println("Error debug ${e.message}")
            null
        }
        return createdMP
    }

    override fun getCurrentPosition(): Int = mp?.currentPosition ?: 0

    override fun getDuration(): Int = mp?.duration ?: 0

    override fun changeSong(uri: String){
        if (mp != null) { stopMusic() }
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
                isPlaying = mp.isPlaying,
                currentPosition = mp.currentPosition
            )
        }
    }
}
/*

@Singleton
class MediaPlayerMusicController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicInfoProvider: MusicInfoProvider
) : MusicController {
    private var mp: MediaPlayer? = null

    override fun playMusic(uri: String) {
        println("DebugPlay111 in controller")
        if (mp == null) musicInfoProvider.changeCurrentMediaPlayer(uri)
        val currentMP = getCurrentMediaPlayer() ?: return
        if (currentMP.isPlaying) return
        currentMP.start()
    }

    override fun pauseMusic() {
        val currentMP = mp ?: return
        if (!currentMP.isPlaying) return
        mp!!.pause()
    }

    override fun stopMusic() {
        if (mp == null) return
        mp?.stop()
        mp?.release()
        mp = null
    }

    override fun setCurrentTimeInMillis(newTime: Int) {
        mp?.seekTo(newTime)
    }

    private fun createMP(uri: String): MediaPlayer?{
        val createdMP = try {
            MediaPlayer.create(context, Uri.parse(uri))
        } catch (e: Exception){
            println("Error debug ${e.message}")
            null
        }
        return createdMP
    }

    fun test(){
        getCurrentMediaPlayer()?.start()
    }

    private fun getCurrentMediaPlayer() : MediaPlayer?{
        return musicInfoProvider.getCurrentMediaPlayer()
    }

    override fun getCurrentPosition(): Int = mp?.currentPosition ?: 0

    override fun getDuration(): Int = mp?.duration ?: 0

    override fun getIsPlayingMusicState(): Boolean = mp?.isPlaying ?: false

    override fun getInformationForSong(song: SongNew): SongWithDetails?{
        val mp = createMP(song.uri)
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
}*/
