package com.example.fullproject.model.dirpack.entities

data class Directory (
    val id: Long,
    val uri: String,
    val name: String?,
    val disEnableForReading: Boolean?,
    val isDefaultDir: Boolean?)

