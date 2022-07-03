package com.example.fullproject

import android.app.Application
import android.net.Uri
import com.example.fullproject.businesslogic.SoundServiceMusic
import java.io.File

class App: Application() {
    var songs: ArrayList<Uri>? = null

    init {
        var listOFMusic: Array<File>? = null
        val uris: ArrayList<Uri> = ArrayList()

        val file = File("/storage/emulated/0/Download")
        if (file.isDirectory) listOFMusic = file.listFiles()

        if (listOFMusic != null) {
            for (u in listOFMusic) {
                if (equalsWithSupportFormat(getFormatFile(u.toString())))
                uris.add(Uri.fromFile(u))
            }
            songs = uris
        }
    }

    private fun getFormatFile(string: String): String{
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

    private fun equalsWithSupportFormat(format: String): Boolean{
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
}