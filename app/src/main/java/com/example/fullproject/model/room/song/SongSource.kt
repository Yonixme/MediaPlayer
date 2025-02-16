package com.example.fullproject.model.room.song

import com.example.fullproject.model.room.directory.entities.DirectoryNew
import com.example.fullproject.model.room.song.entities.SongNew

interface SongSource {

    fun getAudioFileFromDirectories(
        directories: List<DirectoryNew>,
        songsFromDb: List<SongNew>
    ): List<SongNew>
}