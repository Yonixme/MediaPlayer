package com.example.fullproject.model.room.song.entities

data class SongNew(
    val id: Long,
    val uri: String,
    val name: String?,
    val author: String?,
    val disEnableAutoPlay: Boolean
)
