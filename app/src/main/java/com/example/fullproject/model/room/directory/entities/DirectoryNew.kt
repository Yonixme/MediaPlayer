package com.example.fullproject.model.room.directory.entities

data class DirectoryNew (
    val id: Long,
    val uri: String,
    val name: String?,
    val disEnableForReading: Boolean,
    val isDefaultDir: Boolean
)