package com.example.fullproject.screens.config

fun convertMillisToMinute(progress: Int): String {
    var seconds:Int = progress / 1000

    val minute: Int
    if (seconds >= 60) {
        minute = seconds / 60
        seconds %= 60
    } else minute = 0

    return if (seconds >= 10) "$minute:$seconds"
    else "$minute:0$seconds"
}