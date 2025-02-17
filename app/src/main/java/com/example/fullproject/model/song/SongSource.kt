package com.example.fullproject.model.song

import com.example.fullproject.model.directory.entities.DirectoryNew
import com.example.fullproject.model.song.entities.Song

interface SongSource {

    fun getAudioFileFromDirectories(
        directories: List<DirectoryNew>,
        songsFromDb: List<Song>
    ): List<Song>
}