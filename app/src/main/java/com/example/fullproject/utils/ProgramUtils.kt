package com.example.fullproject.utils

import androidx.fragment.app.Fragment
import com.example.fullproject.App
import com.example.fullproject.Navigator
import com.example.fullproject.screens.viewmodel.FactoryViewModel

fun millisToMinute(progress: Int): String {
    var seconds:Int = progress / 1000

    val minute: Int
    if (seconds > 59) {
        minute = seconds / 60
        seconds %= 60
    } else minute = 0

    return if (seconds > 9) "$minute:$seconds"
    else "$minute:0$seconds"
}

fun getFormatFile(string: String): String{
    val format = string.substring(string.length - 4, string.length)

    return when{
        format[2] == '.' -> {
            format.substring(format.length - 2, format.length)
        }
        format[1] == '.' -> {
            format.substring(format.length - 3, format.length)
        }
        format[0] == '.' -> {
            format
        }
        else -> {
            "No Support Format"
        }
    }
}

fun equalsWithSupportedFormat(format: String): Boolean{
    var isSupportFormat = false
    val arrayFormat = arrayOf(".AA", ".AAC", ".AC3",
        ".ADX", ".AHX", ".AIFF", ".APE", ".AU", ".AUD",
        ".DMF", ".DTS", ".DXD", ".FLAC",
        ".MMF", ".MOD", ".MP1", ".MP2", ".MP3",
        ".MP4", ".MPC", ".Opus", ".RA", ".TTA",
        ".VOC", ".VOX", ".VQF", ".WAV", ".WMA",
        ".XM", ".CD", ".MQA")
    for(f in arrayFormat){
        if(format == f || format == f.lowercase()){
            isSupportFormat = true
            break
        }
    }
    return isSupportFormat
}

fun Fragment.activityNavigator(): Navigator {
    return requireActivity() as Navigator
}

fun Fragment.factory() = FactoryViewModel(requireContext().applicationContext as App)