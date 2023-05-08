package com.example.fullproject.services.model

import android.net.Uri
import com.example.fullproject.services.model.songpack.entities.MetaDataSong
import com.example.fullproject.services.model.songpack.entities.Song
import com.example.fullproject.services.model.songpack.entities.SongPackage

interface SongMapper<T> {
    fun map(): T

    class Base(private val uri: Uri) : SongMapper<Song>{
        override fun map() : Song {
            return Song(uri)
        }
    }

    class MetaData(private val uri: String,
                   private var name: String? = null,
                   private var author: String? = null,
                   private var description: String? = null,
                   private var addToStackPlaying: Boolean = true
    ) : SongMapper<MetaDataSong>{
        override fun map(): MetaDataSong {
            if (name == null || name!!.isBlank()) {
                name = uri
            }
            return MetaDataSong(-1, uri, name!!, author, description, addToStackPlaying)
        }
    }

    class MDSongToSong(private val metaDataSong: MetaDataSong) : SongMapper<Song>{
        override fun map(): Song {
            return Song(Uri.parse(metaDataSong.uri))
        }
    }

    class SongPackageToSong(private val songPackage: SongPackage): SongMapper<Song>{
        override fun map(): Song {
            return Song(songPackage.uri)
        }
    }
}