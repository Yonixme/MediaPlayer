package com.example.fullproject.model.songpack.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Song(val uri: Uri)

@Parcelize
data class SongPackage(val uri: Uri): Parcelable

