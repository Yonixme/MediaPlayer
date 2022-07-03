package com.example.fullproject.tasks

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
