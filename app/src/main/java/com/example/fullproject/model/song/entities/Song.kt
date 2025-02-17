package com.example.fullproject.model.song.entities

data class Song(
    val id: Long,
    val uri: String,
    val name: String?,
    val author: String?,
    val disEnableAutoPlay: Boolean
)
