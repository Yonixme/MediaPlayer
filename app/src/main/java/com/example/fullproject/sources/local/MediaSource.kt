package com.example.fullproject.sources.local

import android.net.Uri
import com.example.fullproject.model.directory.entities.Directory
import com.example.fullproject.model.song.SongSource
import com.example.fullproject.model.song.entities.Song
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSource @Inject constructor() : SongSource {

    override fun getAudioFileFromDirectories(
        directories: List<Directory>,
        songsFromDb: List<Song>
    ): List<Song> {
        val musicFiles = directories
            .map { File(it.uri) }
            .filter { it.isDirectory }
            .flatMap { it.listFiles()?.toList() ?: emptyList() }
            .filter { file -> isSupportedFormat(getFileExtension(Uri.fromFile(file).path!!)) }

        return musicFiles.map { file ->
            val uri = Uri.fromFile(file).path!!
            findSongByUri(uri, songsFromDb) ?: createDefaultSong(uri)
        }
    }

    private fun getFileExtension(filePath: String): String {
        return filePath.substringAfterLast('.', "").lowercase()
    }

    private fun createDefaultSong(uri: String): Song {
        return Song(
            id = -1,
            uri = uri,
            name = null,
            author = null,
            disEnableAutoPlay = false
        )
    }

    private fun findSongByUri(uri: String, list: List<Song>): Song? {
        return list.find { it.uri == uri }
    }

    private fun isSupportedFormat(extension: String): Boolean {
        val supportedFormats = setOf(
            "aa", "aac", "ac3", "adx", "ahx", "ape", "au", "aud", "dmf", "dts",
            "dxd", "flac", "mmf", "mod", "mp1", "mp2", "mp3", "mp4", "mpc", "opus",
            "ra", "tta", "voc", "vox", "vqf", "wav", "wma", "xm", "cd", "mqa"
        )
        return extension in supportedFormats
    }
}

//
//@Singleton
//class MediaSource @Inject constructor(
//    @ApplicationContext private val context: Context
//) : SongSource {
//
//    override fun getAudioFileFromDirectories(
//        directories: List<DirectoryNew>,
//        songsFromDb: List<Song>
//    ): List<Song> {
//        println("debug in source start")
//        if (shouldSkipQuery()) {
//            println("debug in source if")
//            return emptyList()
//        }
//        println("debug in source after if")
//
//        val mediaFiles = mutableListOf<Song>()
//
//        for (directory in directories) {
//            val dirFile = getDirectoryFile(directory.uri)
//            if (dirFile?.isDirectory == true) {
//                val files = dirFile.listFiles()?.filter { isSupportedFormat(it.extension) } ?: emptyList()
//                for (file in files) {
//                    val uri = Uri.fromFile(file).toString()
//                    val song = findSongByUri(uri, songsFromDb) ?: createDefaultSong(uri, file.name)
//                    mediaFiles.add(song)
//                }
//            }
//        }
//        mediaFiles.forEach {
//            println("debug in source $it")
//        }
//
//        return mediaFiles
//    }
//
//    private fun getDirectoryFile(uri: String): File? {
//        return when (uri) {
//            "Download" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//            "Music" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//            else -> File(uri).takeIf { it.exists() }
//        }
//    }
//
//    private fun shouldSkipQuery(): Boolean {
//        return if (Build.VERSION.SDK_INT <= 32) {
//            ContextCompat.checkSelfPermission(
//                context, Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        } else false
//    }
//
//    private fun createDefaultSong(uri: String, name: String): Song {
//        return Song(
//            id = -1,
//            uri = uri,
//            name = name,
//            author = null,
//            disEnableAutoPlay = false
//        )
//    }
//
//    private fun findSongByUri(uri: String, list: List<Song>): Song? {
//        return list.find { it.uri == uri }
//    }
//
//    private fun isSupportedFormat(mimeType: String): Boolean {
//        return mimeType.startsWith("audio/")
//    }
//
//    companion object {
//        private const val REQUEST_CODE = 1001
//    }
//}


////////////////////////////////////////////////////
//    fun getAllMediaFiles(): List<MediaFile> {
//        // Not having permission on < 33 makes the app crash
//        // when attempting to query
//        val skipQuery = if(Build.VERSION.SDK_INT <= 32) {
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        } else false
//
//        if(skipQuery) {
//            return emptyList()
//        }
//
//        val mediaFiles = mutableListOf<MediaFile>()
//
//        val queryUri = if(Build.VERSION.SDK_INT >= 29) {
//            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        } else MediaStore.Files.getContentUri("external")
//
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.DISPLAY_NAME,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//        )
//
//        context.contentResolver.query(
//            queryUri,
//            projection,
//            null,
//            null,
//            null
//        )?.use { cursor ->
//            val idColumn = cursor.getColumnIndexOrThrow(
//                MediaStore.Files.FileColumns._ID
//            )
//            val nameColumn = cursor.getColumnIndexOrThrow(
//                MediaStore.Files.FileColumns.DISPLAY_NAME
//            )
//            val mimeTypeColumn = cursor.getColumnIndexOrThrow(
//                MediaStore.Files.FileColumns.MIME_TYPE
//            )
//
//            while(cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val name = cursor.getString(nameColumn)
//                val mimeType = cursor.getString(mimeTypeColumn)
//
//                if(name != null && mimeType != null) {
//                    val contentUri = ContentUris.withAppendedId(
//                        queryUri,
//                        id
//                    )
//                    val mediaType = when {
//                        mimeType.startsWith("audio/") -> MediaType.AUDIO
//                        else -> MediaType.UNSUPPORTED_FORMAT
//                    }
//
//                    mediaFiles.add(
//                        MediaFile(
//                            uri = contentUri,
//                            name = name,
//                            type = mediaType
//                        )
//                    )
//                }
//            }
//        }
//
//        return mediaFiles.toList()
//    }
