package com.example.fullproject

import androidx.fragment.app.Fragment
import com.example.fullproject.businesslogic.SongMusic

fun Fragment.navigator(): Navigator{
    return requireActivity() as Navigator
}

interface Navigator {
    fun goBack()

    fun onMusicPlaylist(currentTime: Long, song: SongMusic)
}