package com.example.fullproject.model.directory.entities

data class Directory (
    val id: Long,
    val uri: String,
    val name: String?,
    val disEnableForReading: Boolean,
    val isDefaultDir: Boolean
)