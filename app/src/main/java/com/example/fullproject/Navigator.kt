package com.example.fullproject

import com.example.fullproject.model.songpack.entities.SongPackage

interface Navigator {
    fun goBack()

    fun onMusicPlaylist(song: SongPackage)

    fun onDataBaseList()
}