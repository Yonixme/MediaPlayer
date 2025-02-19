package com.example.fullproject.model.song.entities


data class SongWithDetails(
    val song: Song,
    val duration: Int,
    val isPlaying: Boolean,
    val currentPosition: Int
)