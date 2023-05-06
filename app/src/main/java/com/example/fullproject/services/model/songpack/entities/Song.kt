package com.example.fullproject.services.model.songpack.entities

import android.net.Uri

data class Song(val uri: Uri)

data class MetaDataSong(val id: Long, val uri: String, val name: String?, val author: String?,
                        val description: String?, val addToStackPlaying: Boolean)