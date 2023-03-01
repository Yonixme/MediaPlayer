package com.example.fullproject.businesslogic

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SongMusic(val uri: Uri, var isPlay: Boolean = false): Parcelable

class Ssss(val uri: Uri)