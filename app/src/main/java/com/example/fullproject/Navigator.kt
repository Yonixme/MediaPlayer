package com.example.fullproject

import androidx.fragment.app.Fragment
import com.example.fullproject.services.model.SongMusic

fun Fragment.activityNavigator(): Navigator{
    return requireActivity() as Navigator
}

interface Navigator {
    fun goBack()

    fun onMusicPlaylist(currentTime: Long, song: SongMusic)

    fun onDataBaseList()
}