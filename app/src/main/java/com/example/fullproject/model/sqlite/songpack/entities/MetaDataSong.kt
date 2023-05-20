package com.example.fullproject.model.sqlite.songpack.entities

data class MetaDataSong(val id: Long, val uri: String, val name: String?, val author: String?,
                        val description: String?, val addToStackPlaying: Boolean)