package com.example.fullproject.sources.local

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.fullproject.model.directory.entities.DirectoryNew
import com.example.fullproject.model.song.SongSource
import com.example.fullproject.model.song.entities.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSource @Inject constructor(
    @ApplicationContext private val context: Context
) : SongSource {

    override fun getAudioFileFromDirectories(
        directories: List<DirectoryNew>,
        songsFromDb: List<Song>
    ): List<Song> {
        val songsMap = songsFromDb.associateBy { it.uri }
        return directories
            .flatMap { directory ->
                getFilesFromDirectory(File(directory.uri))
                    .filter { isSupportedAudioFile(it) }
                    .mapNotNull { file ->
                        val uri = getContentUri(file) ?: return@mapNotNull null
                        songsMap[uri] ?: createDefaultSong(uri, file.name)
                    }
            }
    }

    private fun getFilesFromDirectory(directory: File): Sequence<File> {
        return directory.walk()
            .onEnter { it.isDirectory && it.listFiles()?.isNotEmpty() ?: false }
            .filter { it.isFile }
    }

    private fun isSupportedAudioFile(file: File): Boolean {
        val extension = file.extension.lowercase()
        return SUPPORTED_FORMATS.contains(extension)
    }

    private fun getContentUri(file: File): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.getMediaUri(context, Uri.fromFile(file)).toString()
        } else {
            Uri.fromFile(file).toString()
        }
    }

    private fun createDefaultSong(uri: String, fileName: String): Song {
        return Song(
            id = -1,
            uri = uri,
            name = fileName.substringBeforeLast('.'),
            author = null,
            disEnableAutoPlay = false
        )
    }

    private companion object {
        val SUPPORTED_FORMATS = setOf(
            "aa", "aac", "ac3", "adx", "ahx", "ape", "au", "aud",
            "dmf", "dts", "dxd", "flac", "mmf", "mod", "mp1", "mp2",
            "mp3", "mp4", "mpc", "opus", "ra", "tta", "voc", "vox",
            "vqf", "wav", "wma", "xm", "cd", "mqa"
        )
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
