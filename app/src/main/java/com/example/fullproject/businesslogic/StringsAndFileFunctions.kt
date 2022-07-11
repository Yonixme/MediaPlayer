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

fun getFormatFile(string: String): String{
    val format = string.substring(string.length - 4, string.length)

    if (format[2] == '.'){
        return format.substring(format.length - 2, format.length)
    }else {
        if (format[1] == '.') {
            return format.substring(format.length - 3, format.length)
        } else
            if (format[0] == '.') {
                return format
            } else {
                return "No Support Format"
            }
    }


}

fun equalsWithSupportFormat(format: String): Boolean{
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