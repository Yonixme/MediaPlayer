package com.example.fullproject.model.song

import com.example.fullproject.model.directory.entities.Directory
import com.example.fullproject.model.song.entities.Song

interface SongSource {

    fun getAudioFileFromDirectories(
        directories: List<Directory>,
        songsFromDb: List<Song>
    ): List<Song>
}