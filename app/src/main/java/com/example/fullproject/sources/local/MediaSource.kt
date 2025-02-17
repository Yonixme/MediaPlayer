package com.example.fullproject.sources.local

import android.net.Uri
import com.example.fullproject.model.directory.entities.DirectoryNew
import com.example.fullproject.model.song.SongSource
import com.example.fullproject.model.song.entities.Song
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSource @Inject constructor(

) : SongSource {

    override fun getAudioFileFromDirectories(
        directories: List<DirectoryNew>,
        songsFromDb: List<Song>
    ): List<Song> {
        val listOFMusic = mutableListOf<File>()
        val listFile = mutableListOf<File>()
        val uris = mutableListOf<Song>()

        for (directory in directories) listFile.add(File(directory.uri))
        for (file in listFile) if (file.isDirectory && file.listFiles() != null) listOFMusic.addAll(
            file.listFiles()!!
        )

        for (mediaFile in listOFMusic) {
            if (equalsWithSupportedFormat(getFormatFile(mediaFile.path.toString()))) {
                val uri = Uri.fromFile(mediaFile).toString()

                val song = findURIInList(
                    uri = uri,
                    list = songsFromDb
                ) ?: defaultSongNew(uri)

                uris.add(song)
            }
        }
        return uris
    }

    private fun getFormatFile(string: String): String {
        val maxCharCount = 5
        val format = string.substring(string.length - maxCharCount, string.length)

        var indexFirstChar = -1
        var i = 0
        for (c in format) {
            if (c == '.') indexFirstChar = i
            i++
        }
        if (indexFirstChar == -1) return "No Support Format"
        return format.substring(format.length - (maxCharCount - indexFirstChar), format.length)
    }

    private fun defaultSongNew(uri: String): Song {
        return Song(
            id = -1,
            uri = uri,
            name = null,
            author = null,
            disEnableAutoPlay = false
        )
    }

    private fun findURIInList(uri: String, list: List<Song>): Song? {
        val song = list.filter { songInList -> songInList.uri == uri }
        return if (song.isNotEmpty()) song.first() else null
    }

    private fun equalsWithSupportedFormat(format: String): Boolean{
        var isSupportFormat = false
        val arrayFormat = arrayOf(".AA", ".AAC", ".AC3",
            ".ADX", ".AHX", ".APE", ".AU", ".AUD",
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
