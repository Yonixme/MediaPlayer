package com.example.fullproject.model.song.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fullproject.model.song.entities.SongData
import com.example.fullproject.model.song.entities.Song

@Entity(tableName = "songs",
    indices =[
        Index("uri", unique = true)
    ]
)
data class SongDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val uri: String,
    val name: String?,
    val author: String?,
    @ColumnInfo(name = "dis_enable_auto_play") val disEnableAutoPlay: Boolean?
) {
    fun toSong(): Song {
        return Song(
            id = id,
            uri = uri,
            name = name,
            author = author,
            disEnableAutoPlay = disEnableAutoPlay ?: false
        )
    }

    companion object{
        fun fromSongData(songData: SongData): SongDbEntity {
            return SongDbEntity(
                id = 0,
                uri = songData.uri,
                name = songData.name,
                author = songData.author,
                disEnableAutoPlay = songData.disEnableAutoPlay
            )
        }

        fun fromSong(song: Song): SongDbEntity {
            return SongDbEntity(
                id = song.id,
                uri = song.uri,
                name = song.name,
                author = song.author,
                disEnableAutoPlay = song.disEnableAutoPlay
            )
        }
    }
}