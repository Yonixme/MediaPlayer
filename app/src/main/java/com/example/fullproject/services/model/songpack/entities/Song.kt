package com.example.fullproject.services.model.songpack.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Song(val uri: Uri)

data class MetaDataSong(val id: Long, val uri: String, val name: String?, val author: String?,
                        val description: String?, val addToStackPlaying: Boolean)

@Parcelize
data class SongPackage(val uri: Uri): Parcelable