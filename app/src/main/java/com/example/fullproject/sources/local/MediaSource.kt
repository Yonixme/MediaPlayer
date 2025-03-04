package com.example.fullproject.sources.local

import android.net.Uri
import com.example.fullproject.model.directory.entities.Directory
import com.example.fullproject.model.song.SongSource
import com.example.fullproject.model.song.entities.Song
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSource @Inject constructor() : SongSource {

    override fun getAudioFileFromDirectories(
        directories: List<Directory>,
        songsFromDb: List<Song>
    ): List<Song> {
        val musicFiles = directories
            .map { File(it.uri) }
            .filter { it.isDirectory }
            .flatMap { it.listFiles()?.toList() ?: emptyList() }
            .filter { file -> isSupportedFormat(getFileExtension(Uri.fromFile(file).path!!)) }

        return musicFiles.map { file ->
            val uri = Uri.fromFile(file).path!!
            findSongByUri(uri, songsFromDb) ?: createDefaultSong(uri)
        }
    }

    private fun getFileExtension(filePath: String): String {
        return filePath.substringAfterLast('.', "").lowercase()
    }

    private fun createDefaultSong(uri: String): Song {
        return Song(
            id = -1,
            uri = uri,
            name = null,
            author = null,
            disEnableAutoPlay = false
        )
    }

    private fun findSongByUri(uri: String, list: List<Song>): Song? {
        return list.find { it.uri == uri }
    }

    private fun isSupportedFormat(extension: String): Boolean {
        val supportedFormats = setOf(
            "aa", "aac", "ac3", "adx", "ahx", "ape", "au", "aud", "dmf", "dts",
            "dxd", "flac", "mmf", "mod", "mp1", "mp2", "mp3", "mp4", "mpc", "opus",
            "ra", "tta", "voc", "vox", "vqf", "wav", "wma", "xm", "cd", "mqa"
        )
        return extension in supportedFormats
    }
}