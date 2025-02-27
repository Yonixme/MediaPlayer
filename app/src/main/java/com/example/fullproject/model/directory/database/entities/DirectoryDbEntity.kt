package com.example.fullproject.model.directory.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fullproject.model.directory.entities.InputDirectoryData
import com.example.fullproject.model.directory.entities.Directory

@Entity(tableName = "directories",
    indices =[
        Index("uri", unique = true)
    ]
)
data class DirectoryDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val uri: String,
    val name: String?,
    @ColumnInfo(name = "dis_enable_for_reading") val disEnableForReading: Boolean?,
    @ColumnInfo(name = "is_default_dir") val isDefaultDir: Boolean?
){
    fun toDirectory(): Directory {
        return Directory(
            id = id,
            uri = uri,
            name = name.orEmpty(),
            disEnableForReading = disEnableForReading ?: false,
            isDefaultDir = isDefaultDir ?: false
        )
    }

    companion object{
        fun fromDirectory(directory: Directory) : DirectoryDbEntity {
            return DirectoryDbEntity(
                id = directory.id,
                uri = directory.uri,
                name = directory.name,
                disEnableForReading = directory.disEnableForReading,
                isDefaultDir = directory.isDefaultDir
            )
        }

        fun fromInputDirectoryData(inputDirectoryData: InputDirectoryData): DirectoryDbEntity {
            return DirectoryDbEntity(
                id = 0,
                uri = inputDirectoryData.uri,
                name = inputDirectoryData.name,
                disEnableForReading = inputDirectoryData.disEnableForReading,
                isDefaultDir = false
            )
        }
    }

}
