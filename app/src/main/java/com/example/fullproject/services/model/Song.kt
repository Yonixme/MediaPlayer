package com.example.fullproject.services.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(val uri: Uri): Parcelable

@Parcelize
data class MetaDataSong(val uri: Uri, val name: String, val author: String?,
                        val description: String?, val addToStackPlaying: Boolean): Parcelable