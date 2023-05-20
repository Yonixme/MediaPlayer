package com.example.fullproject

import com.example.fullproject.model.SongPackage

interface Navigator {
    fun goBack()

    fun onMusicPlaylist(song: SongPackage)

    fun onDataBaseList()
}