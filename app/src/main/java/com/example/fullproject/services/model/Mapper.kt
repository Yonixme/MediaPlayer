package com.example.fullproject.services.model

import android.net.Uri
import com.example.fullproject.businesslogic.SongMusic

interface SongMapper<T> {
    fun map(): T

    class Base(private val uri: Uri) : SongMapper<Song>{
        override fun map() : Song {
            return Song(uri)
        }
    }

    class MetaData(private val uri: Uri,
                   private var name: String? = null,
                   private var author: String? = null,
                   private var description: String? = null,
                   private var addToStackPlaying: Boolean = true
    ) : SongMapper<MetaDataSong>{
        override fun map(): MetaDataSong {
            if (name == null || name!!.isBlank()) {
                name = uri.toString()
            }
            return MetaDataSong(uri, name!!, author, description, addToStackPlaying)
        }
    }

    class MDSongToSong(private val metaDataSong: MetaDataSong) : SongMapper<Song>{
        override fun map(): Song {
            return Song(metaDataSong.uri)
        }
    }
}