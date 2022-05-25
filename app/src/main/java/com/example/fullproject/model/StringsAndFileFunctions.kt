package com.example.fullproject.tasks

import android.os.Environment
import java.io.File

fun millisToMinute(progress: Int): String {
    var seconds: Int = progress / 1000

    val minute: Int
    if (seconds > 59) {
        minute = seconds / 60
        seconds %= 60
    } else minute = 0

    return if (seconds > 9) "$minute:$seconds"
    else "$minute:0$seconds"
}

    fun listFilesWithSubFolders(dir: File): MutableList<File>? {
        val files = mutableListOf<File>()
        for (file in dir.listFiles()) {
            if (file.isDirectory) files.addAll(listFilesWithSubFolders(file)!!) else files.add(file)
        }
        return files
    }

fun listFilesWithSubFolders2(dir: File): MutableList<File>? {
        val files = mutableListOf<File>()
        for (file in dir.listFiles()) {
            if (file.isDirectory)files.add(file)
        }
        return files
    }