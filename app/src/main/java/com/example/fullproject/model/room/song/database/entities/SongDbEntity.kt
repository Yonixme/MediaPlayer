package com.example.fullproject.model.room.song.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fullproject.model.room.song.entities.SongData
import com.example.fullproject.model.room.song.entities.SongNew

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
    fun toSong(): SongNew {
        return SongNew(
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

        fun fromSong(songNew: SongNew): SongDbEntity{
            return SongDbEntity(
                id = songNew.id,
                uri = songNew.uri,
                name = songNew.name,
                author = songNew.author,
                disEnableAutoPlay = songNew.disEnableAutoPlay
            )
        }
    }
}