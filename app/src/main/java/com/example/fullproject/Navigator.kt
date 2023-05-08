package com.example.fullproject

import androidx.fragment.app.Fragment
import com.example.fullproject.services.model.songpack.entities.SongPackage

fun Fragment.activityNavigator(): Navigator{
    return requireActivity() as Navigator
}

interface Navigator {
    fun goBack()

    fun onMusicPlaylist(song: SongPackage)

    fun onDataBaseList()
}