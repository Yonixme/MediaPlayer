package com.example.fullproject.services.model

import android.net.Uri

class Test{

    fun d(){
        val uri = Uri.parse("ssss")
        var song = SongMapper.Base(uri).map()
        var s = SongMapper.MetaData(uri).map()
        var s2 = SongMapper.MetaData(uri, null, null, null, true).map()
        var s3 = SongMapper.MDSongToSong(s2).map()
        var s4 = SongMapper.Base(s3.uri).map()
        var s5 = SongMapper.Base(s.uri).map()
        var s6 = SongMapper.Base(s2.uri).map()
    }
}