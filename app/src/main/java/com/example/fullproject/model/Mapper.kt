package com.example.fullproject.model.songpack.entities

import android.net.Uri
import com.example.fullproject.model.Song
import com.example.fullproject.model.SongPackage
import com.example.fullproject.model.sqlite.songpack.entities.MetaDataSong

interface SongMapper<T> {
    fun map(): T

    class Base(private val uri: Uri) : SongMapper<Song> {
        override fun map() : Song {
            return Song(uri)
        }
    }

    class MDSongToSong(private val metaDataSong: MetaDataSong) : SongMapper<Song> {
        override fun map(): Song {
            return Song(Uri.parse(metaDataSong.uri))
        }
    }

    class SongPackageToSong(private val songPackage: SongPackage): SongMapper<Song> {
        override fun map(): Song {
            return Song(songPackage.uri)
        }
    }

    class MetaData(private val uri: String,
                   private var name: String? = null,
                   private var author: String? = null,
                   private var description: String? = null,
                   private var addToStackPlaying: Boolean = true
    ) : SongMapper<MetaDataSong> {
        override fun map(): MetaDataSong {
            if (name == null || name!!.isBlank()) {
                name = uri
            }
            return MetaDataSong(-1, uri, name, author, description, addToStackPlaying)
        }
    }


}